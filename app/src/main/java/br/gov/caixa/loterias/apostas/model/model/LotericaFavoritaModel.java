package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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

    public void excluirLoterica(Long id, int position,OnSilceListener<RetornoPadraoResponse> listener){
        repository.excluirLoterica(id,listener);
        this.listLotericas.remove(position);
    }

}
