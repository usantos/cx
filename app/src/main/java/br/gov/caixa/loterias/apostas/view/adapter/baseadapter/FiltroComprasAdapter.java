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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.enums.FontCaixaEnum;
import br.gov.caixa.loterias.apostas.utils.FonteUtils;

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
    private int numeroMeioPagamentoSelecionado = 1;//inicia na primeira opcao ("Todos")
    private int numeroSituacaoCompraSelecionada = 1;//inicia na primeira opcao ("Todas")

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
            numeroMeioPagamentoSelecionado = newVal;
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


}

