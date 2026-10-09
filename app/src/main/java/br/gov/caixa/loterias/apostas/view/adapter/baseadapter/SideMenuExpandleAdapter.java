package br.gov.caixa.loterias.apostas.view.adapter.baseadapter;

/**
 * Created by cedesbr450 on 24/01/18.
 */
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import java.util.HashMap;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.enums.FontCaixaEnum;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.DividerUtils;
import br.gov.caixa.loterias.apostas.utils.DrawerEnum;
import br.gov.caixa.loterias.apostas.utils.EspecialUtils;
import br.gov.caixa.loterias.apostas.utils.FonteUtils;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;

public class SideMenuExpandleAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<DrawerEnum> expandableListTitle;
    private HashMap<DrawerEnum, List<DrawerEnum>> expandableListDetail;

    public SideMenuExpandleAdapter(Context context, List<DrawerEnum> expandableListTitle,
                                   HashMap<DrawerEnum, List<DrawerEnum>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final DrawerEnum itemEnum = (DrawerEnum) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item_side_menu, null);
        }
        TextView expandedListTextView = convertView
                .findViewById(R.id.titleItemSideMenu);
        Typeface fonte = FonteUtils.getFonte(FontCaixaEnum.SEMI_BOLD);
        if (fonte != null){
            expandedListTextView.setTypeface(fonte);
        }

        String expandedListText = itemEnum.toString();

        expandedListTextView.setText(expandedListText);
        if (EspecialUtils.isOutubroRosa()){
            expandedListTextView.setTextColor(ContextCompat.getColor(context, R.color.outubro_rosa_secundario));
        }

        List<DrawerEnum> children = expandableListDetail.get(expandableListTitle.get(listPosition));

        StringBuilder description = new StringBuilder();
        description.append(expandedListText);

        if (children != null && children.size() > 1) {
            if (expandedListPosition == 0) {
                description.append(". ").append(children.size()).append(" itens: início da lista");
            } else if (isLastChild) {
                description.append(". ").append(children.size()).append(" itens: fim da lista");
            }
        }

        expandedListTextView.setContentDescription(description.toString());
        expandedListTextView.setHint("botão");

        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        DrawerEnum drawerEnum = (DrawerEnum) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group_side_menu, null);
        }
        TextView listTitleTextView = convertView
                .findViewById(R.id.titleItemGroupSideMenu);

        Typeface fonte = FonteUtils.getFonte(FontCaixaEnum.SEMI_BOLD);
        if (fonte != null){
            listTitleTextView.setTypeface(fonte);
        }

        String listTitle = drawerEnum.toString();
        listTitleTextView.setText(listTitle);

        StringBuilder description = new StringBuilder();
        description.append(listTitle);
        int totalItens = expandableListTitle != null ? expandableListTitle.size() : 0;


        ImageView imagemMaisOpcoes = convertView.findViewById(R.id.imagemMaisOpcoes);
        ImageView imageViewGroup = convertView.findViewById(R.id.imagemItemGroupSideMenu);
        View layoutItem = convertView.findViewById(R.id.layoutItem);

        switch (drawerEnum) {
            case MENU_MINHA_AREA:
                imageViewGroup.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_menu_minha_area));
                listTitleTextView.setTextColor(context.getResources().getColor(R.color.cinza));
                listTitleTextView.setHint("este botão possue subitem");
                convertView.setBackgroundColor(context.getResources().getColor(R.color.branco));
                imagemMaisOpcoes.setVisibility(View.VISIBLE);
//                convertView.findViewById(R.id.layoutItem).setBackgroundResource(R.drawable.drawer_linha_divisao_bg);

                layoutItem.setBackgroundResource(R.drawable.drawer_linha_divisao_bg);
                DividerUtils.setDividerVisible(layoutItem, true);

                imagemMaisOpcoes.setSelected(isExpanded);
                description.insert(0, isExpanded ? "Expandido: " : "Recolhido: ");
                description.append(": ").append(totalItens).append(" itens, início");
                break;
            case MENU_RESULTADOS:
                imageViewGroup.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_menu_resultados));
                listTitleTextView.setTextColor(context.getResources().getColor(R.color.cinza));
                listTitleTextView.setHint("botão");
                convertView.setBackgroundColor(context.getResources().getColor(R.color.branco));
                imagemMaisOpcoes.setVisibility(View.GONE);
//                convertView.findViewById(R.id.layoutItem).setBackgroundResource(R.drawable.drawer_linha_divisao_bg);
                layoutItem.setBackgroundResource(R.drawable.drawer_linha_divisao_bg);
                DividerUtils.setDividerVisible(layoutItem, true);
                break;
            case MENU_RAPIDAO:
                imageViewGroup.setBackground(ContextCompat.getDrawable(context, R.drawable.menu4_rapidao));
                listTitleTextView.setTextColor(context.getResources().getColor(R.color.cinza));
                listTitleTextView.setHint("botão" + drawerEnum.toString());
                convertView.setBackgroundColor(context.getResources().getColor(R.color.branco));
                imagemMaisOpcoes.setVisibility(View.GONE);
//                convertView.findViewById(R.id.layoutItem).setBackgroundResource(R.drawable.drawer_linha_divisao_bg);
                layoutItem.setBackgroundResource(R.drawable.drawer_linha_divisao_bg);
                DividerUtils.setDividerVisible(layoutItem, true);
                break;
            case MENU_COFERIR_BILHETE:
                imageViewGroup.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_menu_conferir_bilhete));
                listTitleTextView.setTextColor(context.getResources().getColor(R.color.cinza));
                listTitleTextView.setHint("botão");
                convertView.setBackgroundColor(context.getResources().getColor(R.color.branco));
                imagemMaisOpcoes.setVisibility(View.GONE);
//                convertView.findViewById(R.id.layoutItem).setBackgroundResource(R.drawable.drawer_linha_divisao_bg);
                layoutItem.setBackgroundResource(R.drawable.drawer_linha_divisao_bg);
                DividerUtils.setDividerVisible(layoutItem, true);
                break;
            case MENU_JOGO_RESPONSAVEL:
                imageViewGroup.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_menu_jogo_responsavel));
                listTitleTextView.setTextColor(context.getResources().getColor(R.color.cinza));
                listTitleTextView.setHint("este botão possue subitem");
                convertView.setBackgroundColor(context.getResources().getColor(R.color.branco));
                imagemMaisOpcoes.setVisibility(View.VISIBLE);
//                convertView.findViewById(R.id.layoutItem).setBackgroundResource(R.drawable.drawer_linha_divisao_bg);
                layoutItem.setBackgroundResource(R.drawable.drawer_linha_divisao_bg);
                DividerUtils.setDividerVisible(layoutItem, true);
                imagemMaisOpcoes.setSelected(isExpanded);
                description.insert(0, isExpanded  ? "Expandido: " : "Recolhido: ");
                break;
            case MENU_REPASSES_SOCIAIS:
                imageViewGroup.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_menu_repasses_socieais));
                listTitleTextView.setTextColor(context.getResources().getColor(R.color.cinza));
                listTitleTextView.setHint("botão");
                convertView.setBackgroundColor(context.getResources().getColor(R.color.branco));
                imagemMaisOpcoes.setVisibility(View.GONE);
//                convertView.findViewById(R.id.layoutItem).setBackgroundResource(R.drawable.drawer_linha_divisao_bg);
                layoutItem.setBackgroundResource(R.drawable.drawer_linha_divisao_bg);
                DividerUtils.setDividerVisible(layoutItem, true);
                break;
            case MENU_TERMO_USO:
                imageViewGroup.setBackground(ContextCompat.getDrawable(context, R.drawable.menu_termo_uso));
                listTitleTextView.setTextColor(context.getResources().getColor(R.color.cinza));
                listTitleTextView.setHint("botão");
                convertView.setBackgroundColor(context.getResources().getColor(R.color.branco));
                imagemMaisOpcoes.setVisibility(View.GONE);
//                convertView.findViewById(R.id.layoutItem).setBackgroundResource(R.drawable.drawer_linha_divisao_bg);
                layoutItem.setBackgroundResource(R.drawable.drawer_linha_divisao_bg);
                DividerUtils.setDividerVisible(layoutItem, true);
                break;
            case MENU_DUVIDAS:
                imageViewGroup.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_questions));
                listTitleTextView.setTextColor(context.getResources().getColor(R.color.cinza));
                listTitleTextView.setHint("botão");
                convertView.setBackgroundColor(context.getResources().getColor(R.color.branco));
                imagemMaisOpcoes.setVisibility(View.GONE);
                layoutItem.setBackgroundResource(R.drawable.drawer_linha_divisao_bg);
                DividerUtils.setDividerVisible(layoutItem, true);
                layoutItem.setBackgroundResource(R.drawable.drawer_linha_divisao_bg);
//                convertView.findViewById(R.id.layoutItem).setBackgroundResource(R.drawable.drawer_linha_divisao_bg);
                break;
            case MENU_SOBRE_CAIXA:
                imageViewGroup.setBackground(ContextCompat.getDrawable(context, R.drawable.link));
                listTitleTextView.setTextColor(context.getResources().getColor(R.color.cinza));
                listTitleTextView.setHint("botão");
                convertView.setBackgroundColor(context.getResources().getColor(R.color.branco));
                imagemMaisOpcoes.setVisibility(View.GONE);
                description.append(": ").append(totalItens).append(" itens, final");
                layoutItem.setOnClickListener(view ->
                    DialogUtils.dialogConfirmar(
                            context,
                            context.getString(R.string.caixa_loterias),
                            (dialog, which) -> {
                                String url = "https://www.caixa.gov.br/sobre-a-caixa/governanca-corporativa/caixa-loterias/Paginas/default.aspx";
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                context.startActivity(i);
                            }
                    )
                );

                break;
            default:
                break;
        }

        listTitleTextView.setContentDescription(description.toString());
        if (EspecialUtils.isOutubroRosa()){
            listTitleTextView.setTextColor(ContextCompat.getColor(context, R.color.outubro_rosa_secundario));
            imagemMaisOpcoes.setBackground(VectorUtils.getShape(R.drawable.ic_seta, R.color.outubro_rosa_secundario));
            imageViewGroup.getBackground().setTint(ContextCompat.getColor(context, R.color.outubro_rosa_secundario));
        }
        convertView.setFocusable(false);
        convertView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

}
