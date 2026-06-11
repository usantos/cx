package br.gov.caixa.loterias.apostas.view.adapter.baseadapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import androidx.appcompat.app.AlertDialog;
import br.gov.caixa.loterias.apostas.R;

import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacoesCompraDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacoesCompraResponse;
import br.gov.caixa.loterias.apostas.model.enums.FontCaixaEnum;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.FonteUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;
import com.android.volley.VolleyError;

public class FiltroComprasAdapter extends BaseExpandableListAdapter {

    public static final int MEIO_PAGAMENTO = 0;
    public static final int SITUACAO = 1;
    private boolean isMeioPagamentoAlterado = false;
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
    private int numeroMeioPagamentoSelecionado = 1;//inicia na primeira opcao ("Todos")
    private int numeroSituacaoCompraSelecionada = 1;//inicia na primeira opcao ("Todas")
    private Long idMeioPagamentoSelecionado, idSituacaoCompraSelecionada;

    public FiltroComprasAdapter(Context context, List<String> expandableListTitle, HashMap<String, List<String>> expandableListDetail,
                                Activity activity, LinkedHashMap<Long, String> situacoesCompra,LinkedHashMap<Long, String> meiosPagamento, Long idMeioPagamento, Long idSituacaoCompra) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
        this.situacoesCompra = situacoesCompra;
        this.meiosPagamento = meiosPagamento;
        this.idMeioPagamentoSelecionado = idMeioPagamento;
        this.idSituacaoCompraSelecionada = idSituacaoCompra;
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
                        if(idMeioPagamentoSelecionado != null && idMeioPagamentoSelecionado > 0){
                            int numeroMeioPagamento = 1;
                            for(Long idMeioPagamento : meiosPagamento.keySet()){
                                if(idMeioPagamento.equals(idMeioPagamentoSelecionado)){
                                    numeroMeioPagamentoSelecionado = numeroMeioPagamento;
                                    npMeioPagamento.setValue(numeroMeioPagamento);
                                    break;
                                }
                                numeroMeioPagamento++;
                            }
                        }
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
                        if(idSituacaoCompraSelecionada != null && idSituacaoCompraSelecionada > 0){
                            int numeroSituacaoCompra = 1;
                            for(Long idSituacaoCompra : situacoesCompra.keySet()){
                                if(idSituacaoCompra.equals(idSituacaoCompraSelecionada)){
                                    numeroSituacaoCompraSelecionada = numeroSituacaoCompra;
                                    npSituacao.setValue(numeroSituacaoCompra);
                                    break;
                                }
                                numeroSituacaoCompra++;
                            }
                        }
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
            numeroMeioPagamentoSelecionado = newVal;
            atualizaSituacoesPorMeioDePagamento(getIdMeioPagamentoSelecionado());

        }
    }

    public Long getIdMeioPagamentoSelecionado(){
        return getIdMeioPagamentoESituacaoCompra(meiosPagamento, numeroMeioPagamentoSelecionado);
    }

    public Long getIdSituacaoSelecionada(){
        return getIdMeioPagamentoESituacaoCompra(situacoesCompra, numeroSituacaoCompraSelecionada);
    }

    private Long getIdMeioPagamentoESituacaoCompra(LinkedHashMap<Long, String> map, int numeroSelecionado){
        Set<Long> keySet = map.keySet();
        Long[] keyArray = keySet.toArray(new Long[keySet.size()]);
        if(numeroSelecionado == 1){
            return null;
        }else{
            return keyArray[numeroSelecionado - 1];
        }
    }

    private String getNomeMeioPagamento(LinkedHashMap<Long, String> map, int numeroSelecionado){
        Collection<String> MP = map.values();
        String value = null;
        int index = 0;
        Iterator<String> iterator = MP.iterator();
        if(numeroSelecionado == 1){
            return null;
        }else{
            while(iterator.hasNext()){
                value = iterator.next();
                if(index == numeroSelecionado-1){
                    break;
                }
                index++;
            }
            return value;
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
            if(getNpSituacao().getValue() != 1){
                return true;
            }
        }
        if (getNpMeioPagamento() != null) {
            if(getNpMeioPagamento().getValue() != 1) {
                return true;
            }
        }
        return false;    }



    public void limpaFiltros(){
        if(getNpMeioPagamento() != null){
            numeroMeioPagamentoSelecionado = 1;//Opção padrão
            getNpMeioPagamento().setValue(numeroMeioPagamentoSelecionado);
        }

        if(getNpSituacao() != null){
            numeroSituacaoCompraSelecionada = 1; //Opção padrão
            getNpSituacao().setValue(numeroSituacaoCompraSelecionada);
        }
    }

    private class ListenerSituacao implements NumberPicker.OnValueChangeListener {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            numeroSituacaoCompraSelecionada = newVal;
            if(isMeioPagamentoAlterado){
                npSituacao.setMinValue(1);
                npSituacao.setMaxValue(situacoesCompra.size());
                npSituacao.setDisplayedValues(listaSituacoesCompra());
                isMeioPagamentoAlterado = false;
            }
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

    public void atualizaSituacoesPorMeioDePagamento(Long idMeioPagamento){

        final AlertDialog loadViewProgress = LoadingViewLoterias.show(activity);
        try{
            ApostaSilceBO.getInstance().getSituacoesCompra(idMeioPagamento, new RequestListener<SituacoesCompraResponse>() {
                @Override
                public void onResponse(SituacoesCompraResponse response) {
                    loadViewProgress.dismiss();
                    if(response.getPayload() != null){
                        situacoesCompra = montaMapSituacoesCompra(response.getPayload());
                        isMeioPagamentoAlterado = true;
                        Log.e("FiltroComprasAdapter", "O payload da resposta do servico que busca as situações de compra é nulo");
                    }

                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    loadViewProgress.dismiss();
                    Log.e("FiltroComprasAdapter", "Erro ao recuperar as siturações de compra", error);
                    DialogUtils.dialogEntendi(
                            activity,
                            "Erro ao recuperar as situações de compra para o meio de pagamento selecionado"
                    );

                }
            });
        } catch (Exception e){
            loadViewProgress.dismiss();
            Log.e("ListaComprasActivity", "Erro ao tentar acessar o serviço de situações de compra", e);
        }
    }


    private LinkedHashMap<Long, String> montaMapSituacoesCompra(List<SituacoesCompraDTO> listaSituacoes){
        String descricao;
        LinkedHashMap<Long, String> map = new LinkedHashMap<>();
        if(listaSituacoes != null){
            map.put(0L, "Todas");
            for(SituacoesCompraDTO situacaoCompra: listaSituacoes){
                switch (situacaoCompra.getId().intValue()){
                    case 9:
                        descricao = situacaoCompra.getDescricao().replace(" - ", "-");
                        map.put(9L, descricao);
                        break;
                    case 12:
                        descricao = situacaoCompra.getDescricao().replace(" - ", "-");
                        map.put(12L, descricao);
                        break;
                    default:
                        map.put(situacaoCompra.getId(), situacaoCompra.getDescricao());
                }
            }
        }
        return map;
    }
}

