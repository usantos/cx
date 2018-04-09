package caixa.gov.br.loterias.view;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bean.Modalidade;
import caixa.gov.br.loterias.model.bean.SessaoUsuario;
import caixa.gov.br.loterias.model.bo.DadosCorporativosBO;
import caixa.gov.br.loterias.model.bo.DadosCorporativosBO_;
import caixa.gov.br.loterias.model.bo.DadosUsuarioBO;
import caixa.gov.br.loterias.model.bo.DadosUsuarioBO_;
import caixa.gov.br.loterias.model.bo.MensagensNetwork;
import caixa.gov.br.loterias.model.bo.RequestListener;
import caixa.gov.br.loterias.model.bo.keycloak.KeycloakBO_;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametroSimulacao;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametrosSimulacaoResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.RepassometroDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.RepassometroDTOResponse;
import caixa.gov.br.loterias.model.dao.DBLoteriasHelper;
import caixa.gov.br.loterias.utils.EstiloModalidade;
import caixa.gov.br.loterias.utils.ExpandableListDataSideMenu;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.adapter.HomeAdapter;
import caixa.gov.br.loterias.view.adapter.SideMenuExpandleAdapter;
import caixa.gov.br.loterias.view.animation.AnimacaoCalculoRepassesSociais;
import caixa.gov.br.loterias.view.animation.AnimacoesAcessoRapido;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;

@OptionsMenu(R.menu.principal)
@EActivity(R.layout.activity_principal)
public class PrincipalActivity extends AppCompatActivity
        implements DiscreteScrollView.ScrollListener<HomeAdapter.ViewHolder>,
        DiscreteScrollView.OnItemChangedListener<HomeAdapter.ViewHolder> {


    private List<Modalidade> data;
    private List<ParametroSimulacao> modalidadesParaListar;
    private RepassometroDTO repassesSociais;
    private static final int ZXING_CAMERA_PERMISSION = 1;
    private DBLoteriasHelper dbLoteriasHelper;
    private View actionApresentarTrevo, actionSettings;

    @ViewById
    protected Toolbar toolbar;

    @ViewById
    protected DrawerLayout drawerLayout;

    @ViewById
    protected Button buttonHomeBet;

    @ViewById
    protected TextView textViewHomeMinimumPurchaseValue, tituloLerBilhetes, textoRepassados, valorRepassesPrincipal;

    @ViewById
    protected RelativeLayout relativeContentPrincipal, relativeBlankView, relativeLayoutContentRapidao, relativeLayoutContentLerBilhetes, relativeLayoutContentFavoritas, relativeLayoutContentRepassados,
            relativeLayoutCarrinho;

    @ViewById
    protected LinearLayout atalhoRapidoLoterias;

    @ViewById
    public DiscreteScrollView homeCarousel;

    @ViewById
    protected ExpandableListView sideMenuExpandableListView;

    @AfterViews
    public void init() {
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toolbarListener();
        layoutSideMenu();
        callWebservice();
        iniciarBancoLocal();
    }

    private void iniciarBancoLocal() {
        dbLoteriasHelper = new DBLoteriasHelper(getApplicationContext());
        dbLoteriasHelper.getReadableDatabase();
    }

    private void montarCarrossel() {
        data = montaListaModalidades();
        homeCarousel.setAdapter(new HomeAdapter(this, this, data, buttonHomeBet));
        homeCarousel.addScrollListener(this);
        homeCarousel.addOnItemChangedListener(this);
        homeCarousel.scrollToPosition(0);
        homeCarousel.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());
    }

    private void callWebservice() {
        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);

        final DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(PrincipalActivity.this);

        dadosCorporativosBO.parametrosSimulacao(new RequestListener<ParametrosSimulacaoResponse>() {
            @Override
            public void onResponse(ParametrosSimulacaoResponse response) {
                loadViewProgress.dismiss();

                modalidadesParaListar = response.getPayload().getParametros();
                if (modalidadesParaListar == null || modalidadesParaListar.isEmpty()) {
                    ViewUtils.alert(PrincipalActivity.this, getString(R.string.sistema_indisponivel), new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            startActivity(new Intent(PrincipalActivity.this, LoginActivity.class));
                            finish();
                        }
                    });
                    return;
                }

                SessaoUsuario sessaoUser = SessaoUsuario.getInstance();
                sessaoUser.setValorMinimimoAposta(response.getPayload().getValorMinimoCarrinho());
                sessaoUser.setValorMaximoAposta(response.getPayload().getValorLimiteDiario());
                sessaoUser.setParametrosSimulacao(response.getPayload());

                textViewHomeMinimumPurchaseValue.setText(String.format("valor mínimo da compra: %s", ViewUtils.getMoedaFormat(response.getPayload().getValorMinimoCarrinho())));

                montaListaModalidades();
                montarCarrossel();

                callRepassesSociais();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                ViewUtils.alert(PrincipalActivity.this, MensagensNetwork.setMensagem(error, getApplicationContext()), new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        startActivity(new Intent(PrincipalActivity.this, LoginActivity.class));
                        finish();
                    }
                });
            }
        });
    }

    private void callRepassesSociais() {
        final DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(PrincipalActivity.this);
        dadosCorporativosBO.repasses(new RequestListener<RepassometroDTOResponse>() {
            @Override
            public void onResponse(RepassometroDTOResponse response) {
                repassesSociais = response.getPayload();
                layoutTrevoAcessoRapido();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                ViewUtils.alert(PrincipalActivity.this, MensagensNetwork.setMensagem(error, getApplicationContext()));
            }
        });
    }

    private List<Modalidade> montaListaModalidades() {
        List<Modalidade> listaModalidades = new ArrayList<>();

        for (ParametroSimulacao parametro : modalidadesParaListar) {
            if (parametro.getParametroJogo().getConcurso().getModalidade() != null) {
                listaModalidades.add(new Modalidade(parametro.getParametroJogo().getConcurso().getModalidadeDetalhada().getDescricao(),
                        parametro.getParametroJogo().getConcurso().getValorApostaMinima(), "", "", parametro.getParametroJogo().getConcurso().getModalidade(), parametro.getParametroJogo().getConcurso(), false, null));
            }
        }

        return listaModalidades;
    }

    private void layoutTrevoAcessoRapido() {
        valorRepassesPrincipal.setText("0");

        tituloLerBilhetes.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.label_ler_bilhestes_bold)));
        textoRepassados.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.label_repassados_boas_causas_bold)));
        AnimacaoCalculoRepassesSociais.start(valorRepassesPrincipal, 0, 0, repassesSociais);
    }

    private void toolbarListener() {
        toolbar.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {

                actionApresentarTrevo = toolbar.findViewById(R.id.action_apresentar_trevo);
                if (actionApresentarTrevo != null) {
                    toolbar.removeOnLayoutChangeListener(this);
                    actionApresentarTrevo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (modalidadesParaListar != null && modalidadesParaListar.size() > 0) {
                                if (relativeContentPrincipal.isEnabled()) {
                                    AnimacaoAbrirMenuRapido(actionApresentarTrevo);
                                } else {
                                    AnimacaoFecharMenuRapido(actionApresentarTrevo);
                                }
                            }
                        }
                    });
                }
                actionSettings = toolbar.findViewById(R.id.action_settings);
                if (actionSettings != null) {
                    toolbar.removeOnLayoutChangeListener(this);
                    actionSettings.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent activity = new Intent(PrincipalActivity.this, TermosUsoActivity.class);
                            startActivity(activity);
                        }
                    });
                }
            }
        });
    }

    private void AnimacaoAbrirMenuRapido(View v) {
        relativeContentPrincipal.setEnabled(false);
        actionApresentarTrevo.setEnabled(false);
        atalhoRapidoLoterias.setVisibility(View.VISIBLE);
        EstiloModalidade estilo = new EstiloModalidade(data.get(homeCarousel.getCurrentItem()).getTipoModalidade());
        AnimacoesAcessoRapido animacoesAcessoRapido = new AnimacoesAcessoRapido(
                v,
                relativeContentPrincipal,
                relativeLayoutContentRapidao,
                relativeLayoutContentLerBilhetes,
                relativeLayoutContentFavoritas,
                relativeLayoutContentRepassados,
                relativeBlankView,
                atalhoRapidoLoterias,
                buttonHomeBet,
                actionApresentarTrevo,
                PrincipalActivity.this,
                estilo
        );
        int positionTopBotao = (int) buttonHomeBet.getY();

        int itemSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 105, this.getResources().getDisplayMetrics());

        animacoesAcessoRapido.AnimacaoDescerContent(positionTopBotao - itemSize);
    }

    private void AnimacaoFecharMenuRapido(View v) {
        relativeContentPrincipal.setEnabled(true);
        actionApresentarTrevo.setEnabled(false);
        EstiloModalidade estilo = new EstiloModalidade(data.get(homeCarousel.getCurrentItem()).getTipoModalidade());
        AnimacoesAcessoRapido animacoesAcessoRapido = new AnimacoesAcessoRapido(
                v,
                relativeContentPrincipal,
                relativeLayoutContentRapidao,
                relativeLayoutContentLerBilhetes,
                relativeLayoutContentFavoritas,
                relativeLayoutContentRepassados,
                relativeBlankView,
                atalhoRapidoLoterias,
                buttonHomeBet,
                actionApresentarTrevo,
                PrincipalActivity.this,
                estilo
        );
        animacoesAcessoRapido.AnimacaoSubirContent();

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void sairLogin() {

        ViewUtils.alertTitleButtonPositiveListener(this, R.string.label_atencao, getResources().getString(R.string.MA008), R.string.label_cotinuar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final AlertDialog loadViewProgress = LoadingViewLoterias.show(PrincipalActivity.this);

                DadosUsuarioBO dadosUsuarioBO = DadosUsuarioBO_.getInstance_(PrincipalActivity.this);
                dadosUsuarioBO.sair(new RequestListener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        KeycloakBO_.getInstance_(PrincipalActivity.this).logout(PrincipalActivity.this);
                        loadViewProgress.dismiss();
                        startActivity(new Intent(PrincipalActivity.this, LoginActivity.class));
                        finish();
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadViewProgress.dismiss();
                        ViewUtils.alert(PrincipalActivity.this, MensagensNetwork.setMensagem(error, getApplicationContext()));
                    }
                });
            }
        });
    }

    @Override
    public void onScroll(
            float currentPosition,
            int currentIndex, int newIndex,
            @Nullable HomeAdapter.ViewHolder currentHolder,
            @Nullable HomeAdapter.ViewHolder newCurrent) {

        if (currentHolder != null && newCurrent != null) {
            float position = Math.abs(currentPosition);
            Log.d("position", "position: " + position);
            Log.d("currentIndex", "currentIndex: " + currentIndex);
            Log.d("newIndex", "newIndex: " + newIndex);
        }

    }

    @Override
    public void onCurrentItemChanged(@Nullable HomeAdapter.ViewHolder viewHolder, int adapterPosition) {
        //viewHolder will never be null, because we never remove items from adapter's list
        if (viewHolder != null) {
            EstiloModalidade estilo = new EstiloModalidade(data.get(adapterPosition).getTipoModalidade());
            viewHolder.setOverlayColor(this, estilo);

            buttonHomeBet.setTag(adapterPosition);
            buttonHomeBet.setText(ViewUtils.textFuturaAndFuturaBold(this, "_Aposte_\npor R$" + modalidadesParaListar.get(adapterPosition).getParametroJogo().getConcurso().getValorApostaMinima()));
        }
    }

    @Click(R.id.buttonHomeBet)
    protected void clickButtonHomeBet() {
        Intent activity = new Intent(PrincipalActivity.this, SimularApostaActivity_.class);
        activity.putExtra("tipoAposta", modalidadesParaListar.get(Integer.parseInt(buttonHomeBet.getTag().toString())).getParametroJogo().getConcurso().getModalidade());
        activity.putExtra("modalidade", (new Gson()).toJson(modalidadesParaListar.get(Integer.parseInt(buttonHomeBet.getTag().toString())).getParametroJogo()));
        startActivity(activity);
    }

    @Click(R.id.relativeLayoutContentRepassados)
    protected void clickRelativeLayoutContentRepassados() {
        AnimacaoFecharMenuRapido(relativeContentPrincipal);
        Intent activity = new Intent(PrincipalActivity.this, RepassesSociaisActivity.class);
        startActivity(activity);
    }

    @Click(R.id.relativeLayoutContentRapidao)
    protected void clickRelativeLayoutContentRapidao() {
        AnimacaoFecharMenuRapido(relativeContentPrincipal);
        startActivity(new Intent(PrincipalActivity.this, ListaRapidaoActivity.class));
    }

    @Click(R.id.relativeLayoutCarrinho)
    protected void clickRelativeLayoutCarrinho() {
        CarrinhoActivity_.intent(PrincipalActivity.this).start();
    }

    @Click(R.id.relativeLayoutContentFavoritas)
    protected void clickRelativeLayoutContentFavoritas() {
        AnimacaoFecharMenuRapido(relativeContentPrincipal);
        startActivity(new Intent(PrincipalActivity.this, FavoritasActivity.class));
    }

    @Click(R.id.relativeLayoutContentLerBilhetes)
    protected void clickRelativeLayoutContentLerBilhetes() {
        if (ContextCompat.checkSelfPermission(PrincipalActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PrincipalActivity.this,
                    new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
        } else {
            AnimacaoFecharMenuRapido(relativeContentPrincipal);
            startActivity(new Intent(PrincipalActivity.this, LerBilhetesActivity.class));
        }
    }

    @Click(R.id.apostasConfirmadasBtn)
    protected void clickApostasConfirmadasBtn() {
        ApostasConfirmadasActivity_.intent(PrincipalActivity.this).start();
    }

    private void AbrirMenusGroup(int groupPosition) {
        if (groupPosition == 1) {
            drawerLayout.closeDrawers();
        } else if (groupPosition == 3) {
            startActivity(new Intent(PrincipalActivity.this, ListaRapidaoActivity.class));
        } else if (groupPosition == 4) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
            } else {
                startActivity(new Intent(PrincipalActivity.this, LerBilhetesActivity.class));
            }
        } else if (groupPosition == 5) {
            startActivity(new Intent(PrincipalActivity.this, RepassesSociaisActivity.class));
        } else if (groupPosition == 6) {
            sairLogin();
        }
    }

    private void AbrirMenusItens(int itemPosition) {
        switch (itemPosition){
            case 0:
                startActivity(new Intent(PrincipalActivity.this, DadosPessoaisActivity.class));
                break;
            case 1:
                startActivity(new Intent(PrincipalActivity.this, ApostasConfirmadasActivity_.class));
                break;
            case 2:
                startActivity(new Intent(PrincipalActivity.this, ListaComprasActivity.class));
                break;
            case 3:
                startActivity(new Intent(PrincipalActivity.this, FavoritasActivity.class));
                break;
            default:
                break;
        }
    }

    private void layoutSideMenu() {

        final ExpandableListAdapter expandableListAdapter;
        final List<String> expandableListTitle;
        final HashMap<String, List<String>> expandableListDetail;

        sideMenuExpandableListView.setGroupIndicator(null);
        expandableListDetail = ExpandableListDataSideMenu.getData();
        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
        expandableListAdapter = new SideMenuExpandleAdapter(this, expandableListTitle, expandableListDetail);
        sideMenuExpandableListView.setAdapter(expandableListAdapter);
        sideMenuExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                AbrirMenusGroup(groupPosition);
            }
        });

        sideMenuExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                AbrirMenusItens(childPosition);
                return false;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case ZXING_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AnimacaoFecharMenuRapido(relativeContentPrincipal);
                    startActivity(new Intent(PrincipalActivity.this, LerBilhetesActivity.class));
                } else {
                    Toast.makeText(this, R.string.label_conceda_permissao_camera, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        dbLoteriasHelper.close();
        super.onDestroy();
    }
}