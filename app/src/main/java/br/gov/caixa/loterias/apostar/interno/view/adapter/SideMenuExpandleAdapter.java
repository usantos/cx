package br.gov.caixa.loterias.apostar.interno.view.adapter;

/**
 * Created by cedesbr450 on 24/01/18.
 */


import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;


public class SideMenuExpandleAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, List<String>> expandableListDetail;

    public SideMenuExpandleAdapter(Context context, List<String> expandableListTitle,
                                   HashMap<String, List<String>> expandableListDetail) {
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
        final String expandedListText = (String) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item_side_menu, null);
        }
        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.titleItemSideMenu);
        expandedListTextView.setText(expandedListText);
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
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group_side_menu, null);
        }
        TextView listTitleTextView = convertView
                .findViewById(R.id.titleItemGroupSideMenu);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);

        ImageView imagemMaisOpcoes = (ImageView) convertView.findViewById(R.id.imagemMaisOpcoes);

        ImageView imageViewGroup = (ImageView) convertView
                .findViewById(R.id.imagemItemGroupSideMenu);
        switch (listPosition) {
            case 0:
                imageViewGroup.setImageResource(R.drawable.menu1_area);
                listTitleTextView.setTextColor(context.getResources().getColor(R.color.cinza));
                convertView.setBackgroundColor(context.getResources().getColor(R.color.branco));
                imagemMaisOpcoes.setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.layoutItem).setBackgroundResource(R.drawable.drawer_linha_divisao_bg);
                imagemMaisOpcoes.setSelected(isExpanded);
                break;
            //Opção vai ser reativada
             /*case 1:

                imageViewGroup.setImageResource(R.drawable.menu3_resultados);
                listTitleTextView.setTextColor(context.getResources().getColor(R.color.cinza));
                convertView.setBackgroundColor(context.getResources().getColor(R.color.branco));
                imagemMaisOpcoes.setVisibility(View.GONE);
                convertView.findViewById(R.id.layoutItem).setBackgroundResource(R.drawable.drawer_linha_divisao_bg);
                break;
                */
            case 1:
                imageViewGroup.setImageResource(R.drawable.menu4_rapidao);
                listTitleTextView.setTextColor(context.getResources().getColor(R.color.cinza));
                convertView.setBackgroundColor(context.getResources().getColor(R.color.branco));
                imagemMaisOpcoes.setVisibility(View.GONE);
                convertView.findViewById(R.id.layoutItem).setBackgroundResource(R.drawable.drawer_linha_divisao_bg);
                break;
            case 2:
                imageViewGroup.setImageResource(R.drawable.menu5_bilhetes);
                listTitleTextView.setTextColor(context.getResources().getColor(R.color.cinza));
                convertView.setBackgroundColor(context.getResources().getColor(R.color.branco));
                imagemMaisOpcoes.setVisibility(View.GONE);
                convertView.findViewById(R.id.layoutItem).setBackgroundResource(R.drawable.drawer_linha_divisao_bg);
                break;
            case 3:
                imageViewGroup.setImageResource(R.drawable.menu6_repasses);
                listTitleTextView.setTextColor(context.getResources().getColor(R.color.cinza));
                convertView.setBackgroundColor(context.getResources().getColor(R.color.branco));
                imagemMaisOpcoes.setVisibility(View.GONE);
                convertView.findViewById(R.id.layoutItem).setBackgroundResource(R.drawable.drawer_linha_divisao_bg);
                break;
            default:
                break;
        }
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
