package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.Serializable;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.Pagina;
import br.gov.caixa.loterias.apostas.model.bean.Paginacao;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PaginacaoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaginacaoFragment extends Fragment {
	public static final String ARG_PAGINACAO = "ARG_PAGINACAO";

	private Paginacao paginacao;

	private TextView subtitulo;
	private TextView numeroPagina;
	private TextView divisor;
	private TextView qtdPagina;

	public PaginacaoFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param paginacao Parameter 1.
	 * @return A new instance of fragment PaginacaoFragment.
	 */
	public static PaginacaoFragment newInstance(Paginacao paginacao) {
		PaginacaoFragment fragment = new PaginacaoFragment();
		Bundle            args     = new Bundle();
		args.putSerializable(ARG_PAGINACAO, (Serializable) paginacao);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			paginacao = (Paginacao) getArguments().getSerializable(ARG_PAGINACAO);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_paginacao, container, false);
		subtitulo = view.findViewById(R.id.subtituloPaginacao);
		numeroPagina = view.findViewById(R.id.numeroPagina);
		divisor = view.findViewById(R.id.divisorPaginacao);
		qtdPagina = view.findViewById(R.id.qtdPagina);
		if (paginacao != null){
			alteraCor(paginacao.getCor());
			iniciaPaginacao(paginacao);
		} else {
			view.setVisibility(View.GONE);
		}
		return view;
	}

	private void alteraCor(int cor) {
		subtitulo.setTextColor(ContextCompat.getColor(getContext(), cor));
		numeroPagina.setTextColor(ContextCompat.getColor(getContext(), cor));
		divisor.setTextColor(ContextCompat.getColor(getContext(), cor));
		qtdPagina.setTextColor(ContextCompat.getColor(getContext(), cor));
	}

	public void iniciaPaginacao(Paginacao paginacao){
		if (paginacao != null && !paginacao.getPaginas().isEmpty()){
			atualizaPaginacao(paginacao.getPaginas().get(0));
			divisor.setText(paginacao.getDivisor());
			qtdPagina.setText(String.valueOf(paginacao.getQtdPagina()));
		}
	}

	private void atualizaPaginacao(Pagina pagina) {
		//SpannableStringBuilder subtituloString = ViewUtils.textFuturaAndFuturaBold(getActivity(), pagina.getSubtitulo());
		subtitulo.setText(pagina.getSubtitulo());
		numeroPagina.setText(String.valueOf(pagina.getNumeroPagina()));
	}

	public void proximaPagina(){
		Pagina proxima = paginacao.getProxima();
		if (proxima != null){
			atualizaPaginacao(proxima);
		}
	}

	public void voltaPagina(){
		Pagina anterior = paginacao.getAnterior();
		if (anterior != null){
			atualizaPaginacao(anterior);
		}
	}

	public void atualizaPaginacao(Paginacao paginacao) {
		this.paginacao = paginacao;
	}
}