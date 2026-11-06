package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;
import android.util.Log;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoResponse;
import br.gov.caixa.loterias.apostas.model.repository.LotericaFavoritaRepository;

public class LotericaFavoritaModel extends AppModel{

	private LotericaFavoritaRepository repository;
    
    private List<LotericaFavoritaDTO> listLotericas;
    private Long idLoterica;
    
    public LotericaFavoritaModel(Activity activity){
        super(activity);
        this.repository = new LotericaFavoritaRepository(getActivity());
        listLotericas = new ArrayList<>();
    }

    public List<LotericaFavoritaDTO> getList(){return listLotericas;}

    public void setListLotericas(List<LotericaFavoritaDTO> listLotericas){
        this.listLotericas = listLotericas;
    }

    public Long getIdLotericaByPosition(int position){
        return listLotericas.get(position).getCodigo();
    }

	public void buscaLotericas(OnSilceListener<List<LotericaFavoritaDTO>> listener) {
        repository.getLotericasFavoritas(listener);
	}

    public void incluirLotericaFavorita(Long idLoterica, OnSilceListener<RetornoPadraoResponse> listener) {
        ApostaSilceBO.getInstance().incluirLotericaFavorita(idLoterica, new RequestListener<RetornoPadraoResponse>() {
            @Override
            public void onResponse(RetornoPadraoResponse response) {
                listener.success(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.error(error);
            }
        });
    }

    public void excluirLotericaFavorita(Long idLoterica, OnSilceListener<RetornoPadraoResponse> listener) {
        ApostaSilceBO.getInstance().excluirLotericaFavorita(idLoterica, new RequestListener<RetornoPadraoResponse>() {

            @Override
            public void onResponse(RetornoPadraoResponse response) {
                listener.success(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.error(error);
            }
        });
    }

}
