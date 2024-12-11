package br.gov.caixa.loterias.apostas.view.adapter.baseadapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacoesCompraDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacoesCompraResponse;
import br.gov.caixa.loterias.apostas.model.enums.FontCaixaEnum;
import br.gov.caixa.loterias.apostas.utils.FonteUtils;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;

public class FiltroComprasAdapter extends BaseExpandableListAdapter {

    public static final int MEIO_PAGAMENTO = 0;
    public static final int SITUACAO = 1;

    private Context context;
    private List<String> expandableListTitle;
    private LinkedHashMap<Long, String> situacoesCompra;
    private LinkedHashMap<Long, String> meiosPagamento;
    private HashMap<String, List<String>> expandableListDetail;
    private NumberPicker npMeioPagamento;
    private NumberPicker npSituacao;
    private Activity activity;
    private View viewMeioPagamento;
    private View viewSituacao;
    private int numeroMeioPagamentoSelecionado;
    private int numeroSituacaoCompraSelecionada;

    public FiltroComprasAdapter(Context context, List<String> expandableListTitle, HashMap<String, List<String>> expandableListDetail,
                                Activity activity, LinkedHashMap<Long, String> situacoesCompra,LinkedHashMap<Long, String> meiosPagamento){
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
        this.situacoesCompra = situacoesCompra;
        this.meiosPagamento = meiosPagamento;
        this.setActivity(activity);
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String listTitle = (String) getGroup(listPosition);

        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group_filtro, null);
        }

        TextView listTitleTextView = convertView.findViewById(R.id.titleItemGroupSideMenu);
        Typeface fontCaixaBold = FonteUtils.getFonte(FontCaixaEnum.BOLD);
        listTitleTextView.setTypeface(fontCaixaBold);
        listTitleTextView.setText(listTitle);

        ImageView imagemMaisOpcoes = convertView.findViewById(R.id.imagemMaisOpcoes);

        listTitleTextView.setTextColor(context.getResources().getColor(R.color.cinza));
        convertView.findViewById(R.id.layoutItem).setBackgroundResource(R.drawable.drawner_linha_divisao_cinza_escuro);

        imagemMaisOpcoes.setSelected(isExpanded);

        return convertView;
    }

    @Override
    public View getChildView(int listPosition, int expandedListPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (listPosition){
            case MEIO_PAGAMENTO:
                if(viewMeioPagamento == null){
                    viewMeioPagamento = layoutInflater.inflate(R.layout.list_item_meio_pagamento_filtro, null);
                    if(npMeioPagamento == null && viewMeioPagamento.findViewById(R.id.np_meio_pagamento) != null){
                        setNpMeioPagamento(viewMeioPagamento.findViewById(R.id.np_meio_pagamento));
                        npMeioPagamento.setMinValue(1);
                        npMeioPagamento.setMaxValue(meiosPagamento.size());
                        npMeioPagamento.setDisplayedValues(listaMeiosPagamento());
                        npMeioPagamento.setOnValueChangedListener(new ListenerMeioPagamento());
                        npMeioPagamento.setWrapSelectorWheel(false);
                    }
                }
                convertView = viewMeioPagamento;
                break;
            case SITUACAO:
                if(viewSituacao == null){
                    viewSituacao = layoutInflater.inflate(R.layout.list_item_situacao_compra, null);
                    if(npSituacao == null && viewSituacao.findViewById(R.id.np_situacao_compra) !=null){
                        setNpSituacao(viewSituacao.findViewById(R.id.np_situacao_compra));
                        npSituacao.setMinValue(1);
                        npSituacao.setMaxValue(situacoesCompra.size());
                        npSituacao.setDisplayedValues(listaSituacoesCompra());
                        npSituacao.setOnValueChangedListener(new ListenerSituacao());
                        npSituacao.setWrapSelectorWheel(false);
                    }
                }
                convertView = viewSituacao;
                break;
        }
        return convertView;
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition)).size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition)).get(expandedListPosition);
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }


    private class ListenerMeioPagamento implements NumberPicker.OnValueChangeListener {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            numeroMeioPagamentoSelecionado = newVal;//verificar. Especialmente o valor inicial que carrega.
            callWebServiceSituacoesCompra(getIdMeioPagamentoSelecionado());
            notifyDataSetChanged();

        }
    }

    public Long getIdMeioPagamentoSelecionado(){
        return getIdMeioPagamentoESituacaoCompra(meiosPagamento);
    }

    public Long getIdSituacaoSelecionada(){
        return getIdMeioPagamentoESituacaoCompra(situacoesCompra);
    }

    private Long getIdMeioPagamentoESituacaoCompra(LinkedHashMap<Long, String> map){
        Set<Long> keySet = map.keySet();
        Long[] keyArray = keySet.toArray(new Long[keySet.size()]);
        if(numeroSituacaoCompraSelecionada == 1){
            return null;
        }else{
            return keyArray[numeroSituacaoCompraSelecionada - 1];
        }
    }


    public String[] listaSituacoesCompra (){
        return situacoesCompra.values().toArray(new String[situacoesCompra.size()]);
    }


    public String[] listaMeiosPagamento (){
        return meiosPagamento.values().toArray(new String[meiosPagamento.size()]);
    }

    public Boolean isAddBadge(){
        if (getNpSituacao() != null) {
            if(getNpSituacao().getValue() != 1){//verificar este dado
                return true;
            }
        }
        if (getNpMeioPagamento() != null) {
            if(getNpMeioPagamento().getValue() != 1) {
                return true;
            }
        }
        return false;
    }

//    public Tupla<List<CompraDTO>, Boolean> getComprasFiltradasMeioPagamento(List<CompraDTO> listaCompras){
//        List<CompraDTO> listaFiltrada = new ArrayList<>();
//        Boolean incrementaContador = false;
//        if(getNpMeioPagamento() != null){
//            int meioPagamento = numeroMeioPagamentoSelecionado;
//            String meioPagamentoSelecionado = meiosPagamento.get(meioPagamento).toLowerCase();
//            if(!meioPagamentoSelecionado.equals("todos")){//Alterar o get(1) pelo elemento que vem todos
//                for(CompraDTO compra: listaCompras){
//                    if(meioPagamentoSelecionado.equals(compra.getSituacao().getDescricao().toLowerCase())){
//                        listaFiltrada.add(compra);
//                    }
//                }
//                incrementaContador = true;
//            }
//        }
//        return new Tupla<>(listaFiltrada, incrementaContador);
//    }
//
//    public Tupla<List<CompraDTO>, Boolean> getComprasFiltradasSituacao(List<CompraDTO> listaCompras){
//        List<CompraDTO> listaFiltrada = new ArrayList<>();
//        Boolean incrementaContador = false;
//
//        if(getNpSituacao() != null){
//            int situacao = numeroSituacaoCompraSelecionada;
//            String situacaoSelecionada = situacoesCompra.get(situacao).toLowerCase();
//            if(!situacaoSelecionada.equals("todas")){//substituir o get(1) pelo elemento que traz todos
//                for(CompraDTO compra:listaCompras){
//                    if(situacaoSelecionada.equals(compra.getSituacao().getDescricao().toLowerCase())){
//                        listaFiltrada.add(compra);
//                    }
//                }
//                incrementaContador = true;
//            }
//        }
//        return new Tupla<>(listaFiltrada, incrementaContador);
//    }

    public void limpaFiltros(){
        if(getNpMeioPagamento() != null){
            numeroMeioPagamentoSelecionado = 1;//Meio de pagamento padrao, que mostra por primeiro no number picker
            getNpMeioPagamento().setValue(numeroMeioPagamentoSelecionado); // seta para mostrar o meio padrao
        }

        if(getNpSituacao() != null){
            numeroSituacaoCompraSelecionada = 1; // situacao padao
            getNpSituacao().setValue(numeroSituacaoCompraSelecionada);// seta para mostrar o meio padrao
        }
    }

    private class ListenerSituacao implements NumberPicker.OnValueChangeListener {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            numeroSituacaoCompraSelecionada = newVal;
        }
    }

    public void setNpMeioPagamento(NumberPicker npMeioPagamento) {
        this.npMeioPagamento = npMeioPagamento;
    }

    public NumberPicker getNpMeioPagamento(){return npMeioPagamento;}


    public void setNpSituacao(NumberPicker npSituacao) {
        this.npSituacao = npSituacao;
    }

    public NumberPicker getNpSituacao(){return npSituacao ;}

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    private void callWebServiceSituacoesCompra(Long idMeioPagamento){

        final AlertDialog loadViewProgress = LoadingViewLoterias.show(context);
        ApostaBO.getInstance().getSituacoesCompra(idMeioPagamento, new RequestListener<SituacoesCompraResponse>() {
            @Override
            public void onResponse(SituacoesCompraResponse response) {
                loadViewProgress.dismiss();
                situacoesCompra = montaMapSituacoesCompra(response.getPayload());
                notifyDataSetChanged();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, getActivity());

            }
        });
    }

    private LinkedHashMap<Long, String> montaMapSituacoesCompra(List<SituacoesCompraDTO> listaSituacoes){
        LinkedHashMap<Long, String> map = new LinkedHashMap<>();
        if(listaSituacoes != null){
            map.put(0L, "Todas");
            for(SituacoesCompraDTO situacaoCompra: listaSituacoes){
                map.put(situacaoCompra.getId(), situacaoCompra.getDescricao());
            }
        }
        return map;
    }

}

