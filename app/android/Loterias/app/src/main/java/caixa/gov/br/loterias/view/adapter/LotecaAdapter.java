package caixa.gov.br.loterias.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;

import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.silce.dto.ParametroEquipe;
import caixa.gov.br.loterias.bo.silce.dto.ParametroPartida;

/**
 * Created by joafilho on 26/12/2017.
 */

public class LotecaAdapter extends BaseAdapter {

    private Context context;
    private List<ParametroPartida> listaPartida;
    private List<Object> listaEquipesEmpates;
    private ParametroEquipe equipeEmpate;

    public LotecaAdapter(Context context, List<Object> listaEquipesEmpates) {
        this.context = context;
        this.listaEquipesEmpates = listaEquipesEmpates;
        this.equipeEmpate = new ParametroEquipe();
        equipeEmpate.setNome(context.getString(R.string.label_empate));
        equipeEmpate.setDescricaoCurta(StringUtils.EMPTY);
    }

    @Override
    public int getCount() {
        return listaEquipesEmpates.size();
    }

    @Override
    public Object getItem(int i) {
        return listaEquipesEmpates.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LotecaHolder holder;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_loteca, null, false);
        }
        holder = new LotecaHolder(view);

        Object objeto = listaEquipesEmpates.get(i);

        if (objeto instanceof ParametroEquipe) {
            loadHolder(holder, R.drawable.time_flamengo, (ParametroEquipe) objeto);
        } else {
            loadHolder(holder, R.drawable.icon_fechar, equipeEmpate);
        }

        return view;
    }

    public class LotecaHolder {

        private ImageView imageView;
        private TextView title;
        private TextView description;

        public LotecaHolder(View itemView) {
            imageView = (ImageView) itemView.findViewById(R.id.item_image);
            title = (TextView) itemView.findViewById(R.id.item_title);
            description = (TextView) itemView.findViewById(R.id.item_description);
        }
    }

    private void loadHolder(LotecaHolder holder, int image, ParametroEquipe equipe) {
        holder.imageView.setImageResource(image);
        holder.title.setText(equipe.getNome());
        holder.description.setText(equipe.getDescricaoCurta());
    }

}
