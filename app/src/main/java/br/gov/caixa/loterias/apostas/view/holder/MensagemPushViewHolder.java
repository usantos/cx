package br.gov.caixa.loterias.apostas.view.holder;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.MessagePush;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnMensagemPushListener;

public class MensagemPushViewHolder extends LoteriasHolder<MessagePush> implements View.OnClickListener {
	private ImageView iconImageView;
	private TextView titleTextView;
	private TextView contentTextView;
	private TextView text_preview;
	private TextView dateTextView;
	private LinearLayout expandedLayout;
	private ConstraintLayout cabecalho;
	private Button buttonCompart, buttonMsgExpandida;
	private ImageButton buttonMsgLidaItem;
	private ImageButton buttonMsgExcluirItem;
	private LinearLayout surfacePush;
	private OnMensagemPushListener listener;


	public MensagemPushViewHolder(View view, OnMensagemPushListener listener) {
		super(view);
		this.listener = listener;
		iconImageView = itemView.findViewById(R.id.icon_ImageView);
		buttonCompart = itemView.findViewById(R.id.buttonCompart);
		buttonMsgExpandida = itemView.findViewById(R.id.buttonMsgExpandida);
		buttonMsgLidaItem = itemView.findViewById(R.id.btn_msg_lida);
		buttonMsgExcluirItem = itemView.findViewById(R.id.btn_msg_eliminar);
		cabecalho = itemView.findViewById(R.id.container_cabecalho);
		titleTextView = itemView.findViewById(R.id.titulo_msgpush);
		contentTextView = itemView.findViewById(R.id.mensagem_texto);
		dateTextView = itemView.findViewById(R.id.data_texto);
		expandedLayout = itemView.findViewById(R.id.expandedLayout);
		text_preview = itemView.findViewById(R.id.text_preview);
		surfacePush = itemView.findViewById(R.id.surface_push);

		buttonCompart.setOnClickListener(this);
		buttonMsgExpandida.setOnClickListener(this);
		cabecalho.setOnClickListener(this);
		buttonMsgExcluirItem.setOnClickListener(this);
		buttonMsgLidaItem.setOnClickListener(this);
	}

	@Override
	public void bind(MessagePush item, int position) {
		iconImageView.setImageResource(item.getIcon());
		titleTextView.setText(item.getTitulo());
		TextView contentTextView = expandedLayout.findViewById(R.id.mensagem_texto);
		contentTextView.setText(item.getConteudo());
		text_preview.setText(item.getConteudo());
		dateTextView.setText(item.getData());

		final SwipeLayout swipeLayout = (SwipeLayout) this.itemView;
		swipeLayout.setClickToClose(true);
		swipeLayout.getDragEdgeMap().clear();
		swipeLayout.addDrag(SwipeLayout.DragEdge.Right, swipeLayout.findViewById(R.id.swipeLayoutButtonPush));
		swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);

		swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
			@Override
			public void onClose(SwipeLayout layout) {
				//when the SurfaceView totally cover the BottomView.
			}

			@Override
			public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
				//you are swiping.
			}

			@Override
			public void onStartOpen(SwipeLayout layout) {

			}

			@Override
			public void onOpen(SwipeLayout layout) {
				//when the BottomView totally show.
			}

			@Override
			public void onStartClose(SwipeLayout layout) {

			}

			@Override
			public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
				//when user's hand released.
			}
		});

		expandedLayout.setVisibility(item.isExpanded() ? View.VISIBLE : View.GONE);

		if (item.isRead()) {
			updateMessageViewAsRead();
			item.setSelected(true);
		} else {
			updateMessageViewAsUnread();
		}

		if (item.isSelected()) {
			titleTextView.setTextColor(titleTextView.getResources().getColor(R.color.black));
			titleTextView.setBackgroundColor(titleTextView.getResources().getColor(R.color.branco));
			cabecalho.setBackgroundColor(cabecalho.getResources().getColor(R.color.branco));
			surfacePush.setBackgroundColor(surfacePush.getResources().getColor(R.color.branco));
			iconImageView.setBackgroundColor(surfacePush.getResources().getColor(R.color.branco));
			text_preview.setBackgroundColor(text_preview.getResources().getColor(R.color.branco));
			dateTextView.setBackgroundColor(dateTextView.getResources().getColor(R.color.branco));
		} else {
			cabecalho.setBackgroundColor(cabecalho.getResources().getColor(R.color.pesquisa_msgpush));
			surfacePush.setBackgroundColor(surfacePush.getResources().getColor(R.color.pesquisa_msgpush));
			titleTextView.setBackgroundColor(titleTextView.getResources().getColor(R.color.pesquisa_msgpush));
			titleTextView.setTextColor(titleTextView.getResources().getColor(R.color.cinza));
			iconImageView.setBackgroundColor(iconImageView.getResources().getColor(R.color.pesquisa_msgpush));
			text_preview.setBackgroundColor(text_preview.getResources().getColor(R.color.pesquisa_msgpush));
			dateTextView.setBackgroundColor(dateTextView.getResources().getColor(R.color.pesquisa_msgpush));
		}

		switch (item.getCategoria()) {
			//                case PAGAMENTOS_RESGATES:
			//                    buttonCompart.setVisibility(View.VISIBLE);
			//                    buttonMsgExpandida.setVisibility(View.VISIBLE);
			//                    buttonMsgExpandida.setText("Pagamentos e Resgates");
			//                    buttonMsgExpandida.setTextColor(Color.WHITE);
			//                    buttonMsgExpandida.setBackgroundColor(buttonMsgExpandida.getResources().getColor(R.color.pushbotaoaposteagora));
			//                    break;
			case LEMBRETES_SORTE:
				buttonCompart.setVisibility(View.VISIBLE);
				buttonMsgExpandida.setVisibility(View.VISIBLE);
				buttonMsgExpandida.setTextColor(Color.WHITE);
				buttonMsgExpandida.setText("Apostar agora");
				buttonMsgExpandida.setBackgroundColor(buttonMsgExpandida.getResources().getColor(R.color.pushbotaoaposteagora));
				break;
			case RESULTADOS_DISPONIVEIS:
				buttonMsgExpandida.setVisibility(View.VISIBLE);
				buttonMsgExpandida.setTextColor(Color.WHITE);
				buttonMsgExpandida.setText("Conferir agora");
				buttonMsgExpandida.setBackgroundColor(buttonMsgExpandida.getResources().getColor(R.color.pushbotaoconferiragora));
				break;
			case DICAS_NOVIDADES:
				buttonCompart.setVisibility(View.VISIBLE);
				break;
			default:
				buttonMsgExpandida.setVisibility(View.GONE);
				buttonCompart.setVisibility(View.GONE);
				buttonMsgExpandida.setText("");
				break;
		}

	}

	@Override
	public void onClick(View v) {
		int position = getAdapterPosition();

		switch (v.getId()){
			case R.id.container_cabecalho:
				listener.clickItem(position);
				break;
			case R.id.buttonCompart:
				listener.share(position);
				break;
			case R.id.buttonMsgExpandida:
				DialogUtils.dialogEntendi(
						contentTextView.getContext(),
						contentTextView.getResources().getString(R.string.msg_push_botaocategoria)
				);
				break;
			case R.id.btn_msg_eliminar:
				listener.delete(position);
				break;
			case R.id.btn_msg_lida:
				listener.read(position);
				break;
		}
	}

	private void updateMessageViewAsUnread() {
		cabecalho.setBackgroundColor(cabecalho.getResources().getColor(R.color.pesquisa_msgpush));
		surfacePush.setBackgroundColor(surfacePush.getResources().getColor(R.color.pesquisa_msgpush));
		titleTextView.setBackgroundColor(titleTextView.getResources().getColor(R.color.pesquisa_msgpush));
		titleTextView.setTextColor(titleTextView.getResources().getColor(R.color.cinza));
		iconImageView.setBackgroundColor(iconImageView.getResources().getColor(R.color.pesquisa_msgpush));
		text_preview.setBackgroundColor(text_preview.getResources().getColor(R.color.pesquisa_msgpush));
		dateTextView.setBackgroundColor(dateTextView.getResources().getColor(R.color.pesquisa_msgpush));
	}

	private void updateMessageViewAsRead() {
		cabecalho.setBackgroundColor(cabecalho.getResources().getColor(R.color.branco));
		surfacePush.setBackgroundColor(surfacePush.getResources().getColor(R.color.branco));
		text_preview.setBackgroundColor(text_preview.getResources().getColor(R.color.branco));
		titleTextView.setBackgroundColor(titleTextView.getResources().getColor(R.color.branco));
		titleTextView.setTextColor(titleTextView.getResources().getColor(R.color.black));
		iconImageView.setBackgroundColor(iconImageView.getResources().getColor(R.color.branco));
		dateTextView.setBackgroundColor(dateTextView.getResources().getColor(R.color.branco));
	}

	public void setExpanded(boolean isExpanded) {
		if (isExpanded) {
			titleTextView.setTextColor(Color.BLACK);
			titleTextView.setBackgroundColor(titleTextView.getResources().getColor(R.color.branco));
			cabecalho.setBackgroundColor(cabecalho.getResources().getColor(R.color.branco));
			surfacePush.setBackgroundColor(surfacePush.getResources().getColor(R.color.branco));
			titleTextView.setTextColor(titleTextView.getResources().getColor(R.color.black));
			iconImageView.setBackgroundColor(iconImageView.getResources().getColor(R.color.branco));
			text_preview.setBackgroundColor(text_preview.getResources().getColor(R.color.branco));
			dateTextView.setBackgroundColor(dateTextView.getResources().getColor(R.color.branco));
		}
		text_preview.setVisibility(!isExpanded ? View.VISIBLE : View.INVISIBLE);
		expandedLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
	}

}
