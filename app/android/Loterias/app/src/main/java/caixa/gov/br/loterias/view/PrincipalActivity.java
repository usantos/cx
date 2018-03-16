package caixa.gov.br.loterias.view;


import android.Manifest;
import android.animation.ArgbEvaluator;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bean.SessaoUsuario;
import caixa.gov.br.loterias.model.bo.DadosCorporativosBO;
import caixa.gov.br.loterias.model.bo.DadosCorporativosBO_;
import caixa.gov.br.loterias.model.bo.DadosUsuarioBO;
import caixa.gov.br.loterias.model.bo.DadosUsuarioBO_;
import caixa.gov.br.loterias.model.bo.silce.SilceClient;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametroSimulacao;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametrosSimulacaoResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.RepassometroDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.RepassometroDTOResponse;
import caixa.gov.br.loterias.model.dao.DBLoteriasHelper;
import caixa.gov.br.loterias.model.bean.Modalidade;
import caixa.gov.br.loterias.view.animation.AnimacaoCalculoRepassesSociais;
import caixa.gov.br.loterias.view.animation.AnimacoesAcessoRapido;
import caixa.gov.br.loterias.utils.EstiloModalidade;
import caixa.gov.br.loterias.utils.ExpandableListDataSideMenu;
import caixa.gov.br.loterias.model.bo.MensagensNetwork;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.adapter.HomeAdapter;
import caixa.gov.br.loterias.view.adapter.SideMenuExpandleAdapter;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;

public class PrincipalActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DiscreteScrollView.ScrollListener<HomeAdapter.ViewHolder>,
        DiscreteScrollView.OnItemChangedListener<HomeAdapter.ViewHolder>,
        View.OnClickListener {

    private ArgbEvaluator evaluator;
    private int currentOverlayColor;
    private int overlayColor;
    private Toolbar toolbar;
    private RelativeLayout relativePrincipal;
    private RelativeLayout relativeBlankView;
    private Button buttonHomeBet;

    private View itemToolbarTrevo;
    private View itemToolbarSettings;

    private LinearLayout linearAtalhoRapidoLoterias;
    private RelativeLayout relativeLayoutContentRapidao;
    private RelativeLayout relativeLayoutContentLerBilhetes;
    private RelativeLayout relativeLayoutContentFavoritas;
    private RelativeLayout relativeLayoutContentRepassados;
    private List<Modalidade> data;
    private DiscreteScrollView itemCarousel;

    private List<ParametroSimulacao> modalidadesParaListar;
    private RepassometroDTO repassesSociais;

    private DrawerLayout drawerSideMenu;

    private static final int ZXING_CAMERA_PERMISSION = 1;
    private DBLoteriasHelper dbLoteriasHelper;

    TextView valorRepassesSociais;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerSideMenu = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerSideMenu, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerSideMenu.addDrawerListener(toggle);
        toggle.syncState();

        layoutSideMenu();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toolbarListener();
        listenerRepassados();
        listenerCarrinho();
        listenerRapidao();
        listenerFavoritas();
        listenerLerBilhetes();
        listenerApostasConfirmadas();

        callWebservice();
        iniciarBancoLocal();
    }

    private void iniciarBancoLocal() {
        dbLoteriasHelper = new DBLoteriasHelper(getApplicationContext());
        dbLoteriasHelper.getReadableDatabase();
    }


    private void montarCarrossel() {
        evaluator = new ArgbEvaluator();
        currentOverlayColor = ContextCompat.getColor(this, R.color.verdesenaclaro);
        overlayColor = ContextCompat.getColor(this, R.color.cinza);

//        ModalidadesMock modalidade = ModalidadesMock.get();
//        data = modalidade.getData();
        data = montaListaModalidades();
        itemCarousel = (DiscreteScrollView) findViewById(R.id.home_carousel);
        buttonHomeBet = (Button) findViewById(R.id.buttonHomeBet);
        itemCarousel.setAdapter(new HomeAdapter(this, data, findViewById(R.id.buttonHomeBet)));
        itemCarousel.addScrollListener(this);
        itemCarousel.addOnItemChangedListener(this);
        itemCarousel.scrollToPosition(0);
        itemCarousel.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());

        LayoutGeral();
    }

    private void callWebservice() {
        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);

        final DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(PrincipalActivity.this);

        dadosCorporativosBO.parametrosSimulacao(new SilceClient.Listener<ParametrosSimulacaoResponse>() {
            @Override
            public void onResponse(ParametrosSimulacaoResponse response) {
                loadViewProgress.dismiss();

                modalidadesParaListar = response.getPayload().getParametros();
                SessaoUsuario sessaoUser = SessaoUsuario.getInstance();
                sessaoUser.setValorMinimimoAposta(response.getPayload().getValorMinimoCarrinho());
                sessaoUser.setValorMaximoAposta(response.getPayload().getValorLimiteDiario());
                sessaoUser.setParametrosSimulacao(response.getPayload());

                if (modalidadesParaListar == null || modalidadesParaListar.size() == 0) {
                    startActivity(new Intent(PrincipalActivity.this, LoginActivity.class));
                    finish();
                    return;
                }
                TextView valorMinimoCompra = findViewById(R.id.textViewHomeMinimumPurchaseValue);
                valorMinimoCompra.setText("valor mínimo da compra: " + ViewUtils.getMoedaFormat(response.getPayload().getValorMinimoCarrinho()));

                montaListaModalidades();
                montarCarrossel();

                callRepassesSociais();
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                ViewUtils.alert(PrincipalActivity.this, MensagensNetwork.setMensagem(error, getApplicationContext()));
            }
        });
    }

    private void callRepassesSociais() {
        final DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(PrincipalActivity.this);
        dadosCorporativosBO.repasses(new SilceClient.Listener<RepassometroDTOResponse>() {
            @Override
            public void onResponse(RepassometroDTOResponse response) {
                repassesSociais = response.getPayload();
                layoutTrevoAcessoRapido();
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ViewUtils.alert(PrincipalActivity.this, MensagensNetwork.setMensagem(error, getApplicationContext()));
            }
        });
    }

    private List<Modalidade> montaListaModalidades() {
        List<Modalidade> listaModalidades = new ArrayList<Modalidade>();

        for (ParametroSimulacao parametro : modalidadesParaListar) {
            if (parametro.getParametroJogo().getConcurso().getModalidade() != null){
                listaModalidades.add(new Modalidade(parametro.getParametroJogo().getConcurso().getModalidadeDetalhada().getDescricao(),
                        parametro.getParametroJogo().getConcurso().getValorApostaMinima(), "", "", parametro.getParametroJogo().getConcurso().getModalidade(), parametro.getParametroJogo().getConcurso()));
            }
        }

        return listaModalidades;
    }

    private void LayoutGeral() {
        relativePrincipal = (RelativeLayout) findViewById(R.id.RelativeContentPrincipal);
        relativeBlankView = (RelativeLayout) findViewById(R.id.RelativeBlankView);
        linearAtalhoRapidoLoterias = (LinearLayout) findViewById(R.id.atalhoRapidoLoterias);
        relativeLayoutContentRapidao = (RelativeLayout) findViewById(R.id.RelativeLayoutContentRapidao);
        relativeLayoutContentLerBilhetes = (RelativeLayout) findViewById(R.id.RelativeLayoutContentLerBilhetes);
        relativeLayoutContentFavoritas = (RelativeLayout) findViewById(R.id.RelativeLayoutContentFavoritas);
        relativeLayoutContentRepassados = (RelativeLayout) findViewById(R.id.RelativeLayoutContentRepassados);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void layoutTrevoAcessoRapido() {
        String htmlTextViewBilhetesTexto = "Ler _bilhetes_";
        String htmlTextViewRepassados = "_repassados_ para boas causas.";
        TextView TextViewBilhetesTexto = (TextView) findViewById(R.id.tituloLerBilhetes);
        TextView TextViewRepassados = (TextView) findViewById(R.id.textoRepassados);
        valorRepassesSociais = (TextView) findViewById(R.id.valorRepassesPrincipal);
//        valorRepassesSociais.setText(ViewUtils.getMoedaFormat(repassesSociais.getArrecadacaoAcumulada()));
        valorRepassesSociais.setText("0");

        TextViewBilhetesTexto.setText(ViewUtils.textFuturaAndFuturaBold(this, htmlTextViewBilhetesTexto));
        TextViewRepassados.setText(ViewUtils.textFuturaAndFuturaBold(this, htmlTextViewRepassados));
        AnimacaoCalculoRepassesSociais.start(valorRepassesSociais, 0, 0, repassesSociais);
    }

    private void toolbarListener() {
        toolbar.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                itemToolbarTrevo = toolbar.findViewById(R.id.action_apresentar_trevo);
                if (itemToolbarTrevo != null) {
                    toolbar.removeOnLayoutChangeListener(this);
                    itemToolbarTrevo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (modalidadesParaListar != null && modalidadesParaListar.size() > 0) {
                                if (relativePrincipal.isEnabled()) {
                                    AnimacaoAbrirMenuRapido(v);
                                } else {
                                    AnimacaoFecharMenuRapido(v);
                                }
                            }
                        }
                    });
                }
                itemToolbarSettings = toolbar.findViewById(R.id.action_settings);
                if (itemToolbarSettings != null) {
                    toolbar.removeOnLayoutChangeListener(this);
                    itemToolbarSettings.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent activity = new Intent(PrincipalActivity.this, TermosUsoActivity.class);
                            startActivity(activity);
                        }
                    });
                }
            }
        });
    }

    private void AnimacaoAbrirMenuRapido(View v) {
        relativePrincipal.setEnabled(false);
        itemToolbarTrevo.setEnabled(false);
        linearAtalhoRapidoLoterias.setVisibility(View.VISIBLE);
        EstiloModalidade estilo = new EstiloModalidade(data.get(itemCarousel.getCurrentItem()).getTipoModalidade());
        AnimacoesAcessoRapido animacoesAcessoRapido = new AnimacoesAcessoRapido(
                v,
                relativePrincipal,
                relativeLayoutContentRapidao,
                relativeLayoutContentLerBilhetes,
                relativeLayoutContentFavoritas,
                relativeLayoutContentRepassados,
                relativeBlankView,
                linearAtalhoRapidoLoterias,
                buttonHomeBet,
                itemToolbarTrevo,
                PrincipalActivity.this,
                estilo
        );
        int positionTopBotao = (int) buttonHomeBet.getY();

        int itemSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 105, this.getResources().getDisplayMetrics());

        animacoesAcessoRapido.AnimacaoDescerContent(positionTopBotao - itemSize);
    }

    private void AnimacaoFecharMenuRapido(View v) {
        relativePrincipal.setEnabled(true);
        itemToolbarTrevo.setEnabled(false);
        EstiloModalidade estilo = new EstiloModalidade(data.get(itemCarousel.getCurrentItem()).getTipoModalidade());
        AnimacoesAcessoRapido animacoesAcessoRapido = new AnimacoesAcessoRapido(
                v,
                relativePrincipal,
                relativeLayoutContentRapidao,
                relativeLayoutContentLerBilhetes,
                relativeLayoutContentFavoritas,
                relativeLayoutContentRepassados,
                relativeBlankView,
                linearAtalhoRapidoLoterias,
                buttonHomeBet,
                itemToolbarTrevo,
                PrincipalActivity.this,
                estilo
        );
        animacoesAcessoRapido.AnimacaoSubirContent();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_apresentar_trevo) {
            return true;
        }
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_apostar) {

        } else if (id == R.id.nav_bilhetes) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_minhaarea) {

        } else if (id == R.id.nav_rapidao) {

            startActivity(new Intent(PrincipalActivity.this, ListaRapidaoActivity.class));
        } else if (id == R.id.nav_bilhetes) {

        } else if (id == R.id.nav_sociais) {

            startActivity(new Intent(PrincipalActivity.this, RepassesSociaisActivity.class));
        } else if (id == R.id.nav_sair) {
            sairLogin();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sairLogin() {

        ViewUtils.alertTitleButtonPositiveListener(this, R.string.label_atencao, getResources().getString(R.string.MA008), R.string.label_cotinuar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final AlertDialog loadViewProgress = LoadingViewLoterias.show(PrincipalActivity.this);

                DadosUsuarioBO dadosUsuarioBO = DadosUsuarioBO_.getInstance_(PrincipalActivity.this);
                dadosUsuarioBO.sair(new SilceClient.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        loadViewProgress.dismiss();
                        startActivity(new Intent(PrincipalActivity.this, LoginActivity.class));
                        finish();
                    }
                }, new SilceClient.ErrorListener() {
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
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.home:
//                finish();
//                break;
//            case R.id.fab_share:
//                share(v);
//                break;
//        }
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

//            currentHolder.setOverlayColor(interpolate(position, currentOverlayColor, overlayColor));
//            newCurrent.setOverlayColor(interpolate(position, overlayColor, currentOverlayColor));

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

            findViewById(R.id.buttonHomeBet).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent activity = new Intent(PrincipalActivity.this, SimularApostaActivity_.class);
                    activity.putExtra("tipoAposta", modalidadesParaListar.get(Integer.parseInt(view.getTag().toString())).getParametroJogo().getConcurso().getModalidade());
                    activity.putExtra("modalidade", (new Gson()).toJson(modalidadesParaListar.get(Integer.parseInt(view.getTag().toString())).getParametroJogo()));
                    startActivity(activity);
                }
            });
        }
    }

    private void share(View view) {
//        Snackbar.make(view, R.string.msg_unsupported_op, Snackbar.LENGTH_SHORT).show();
    }

    private int interpolate(float fraction, int c1, int c2) {
        return (int) evaluator.evaluate(fraction, c1, c2);
    }

    private void listenerRepassados() {

        RelativeLayout relativerepassados = (RelativeLayout) findViewById(R.id.RelativeLayoutContentRepassados);
        relativerepassados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimacaoFecharMenuRapido(relativePrincipal);

                Intent activity = new Intent(PrincipalActivity.this, RepassesSociaisActivity.class);
//                activity.putExtra("repassesSociais", (new Gson()).toJson(repassesSociais));
                startActivity(activity);
            }
        });
    }


    private void listenerRapidao() {

        RelativeLayout relativerapidao = (RelativeLayout) findViewById(R.id.RelativeLayoutContentRapidao);
        relativerapidao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimacaoFecharMenuRapido(relativePrincipal);
                startActivity(new Intent(PrincipalActivity.this, ListaRapidaoActivity.class));
            }
        });
    }

    private void listenerCarrinho() {

        RelativeLayout relativecarrinho = (RelativeLayout) findViewById(R.id.RelativeLayoutCarrinho);
        relativecarrinho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PrincipalActivity.this, CarrinhoActivity.class));
            }
        });
    }


    private void listenerFavoritas() {

        RelativeLayout relativefavoritas = (RelativeLayout) findViewById(R.id.RelativeLayoutContentFavoritas);
        relativefavoritas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimacaoFecharMenuRapido(relativePrincipal);
                startActivity(new Intent(PrincipalActivity.this, FavoritasActivity.class));
            }
        });
    }

    private void listenerLerBilhetes() {

        RelativeLayout lerBilhetes = (RelativeLayout) findViewById(R.id.RelativeLayoutContentLerBilhetes);
        lerBilhetes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(PrincipalActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PrincipalActivity.this,
                            new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
                } else {
                    AnimacaoFecharMenuRapido(relativePrincipal);
                    startActivity(new Intent(PrincipalActivity.this, LerBilhetesActivity.class));
                }
            }
        });
    }

    private void listenerApostasConfirmadas(){
        findViewById(R.id.apostasConfirmadasBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApostasConfirmadasActivity_.intent(PrincipalActivity.this).start();
            }
        });
    }

    private void AbrirMenusGroup(int groupPosition) {
        if (groupPosition == 0) {

        } else if (groupPosition == 1) {
            drawerSideMenu.closeDrawers();
        } else if (groupPosition == 2) {

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
        if (itemPosition == 0) {

        } else if (itemPosition == 1) {

        } else if (itemPosition == 2) {
            startActivity(new Intent(PrincipalActivity.this, ListaComprasActivity.class));
        } else if (itemPosition == 3) {
            startActivity(new Intent(PrincipalActivity.this, FavoritasActivity.class));
        } else if (itemPosition == 4) {

        }
    }

//    private void animacaoValorRepassesSociais( double oldValue, double calculo ){
//        Date dataBase = new Date(System.currentTimeMillis());
//        SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");
//        try {
//            dataBase  = simpleDate.parse(repassesSociais.getDataBase());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        Date todayDate = new Date();
//        double seconds = (todayDate.getTime() - dataBase.getTime()) / DateUtils.MILLIS_PER_SECOND;
//        oldValue = calculo;
//
//        calculo = (repassesSociais.getArrecadacaoAcumulada().doubleValue()) + (repassesSociais.getArrecadacaoUltimaSemana().doubleValue() / 604800.0) * seconds;
//        oldValue = oldValue == 0 ? repassesSociais.getArrecadacaoAcumulada().doubleValue() : oldValue;
//
//        final double calculoFinal = calculo;
//        final double oldValueFinal = oldValue;
//
//        ValueAnimator animator = new  ValueAnimator();
//        animator.setObjectValues(oldValue, calculo); //double value
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            public void onAnimationUpdate(ValueAnimator animation) {
//                String valorUpdate = String.format( "%.2f", animation.getAnimatedValue());
//                valorUpdate = valorUpdate.replace(",", ".");
//                valorRepassesSociais.setText(ViewUtils.getMoedaFormat(new BigDecimal(valorUpdate)));
//            }
//        });
//        animator.setEvaluator(new TypeEvaluator<Double>() { // problem here
//            @Override
//            public Double evaluate(float fraction, Double startValue, Double endValue) {
//                return  (startValue + (double)((endValue - startValue) * fraction));
//            }
//        });
//        animator.addListener(new AnimatorListenerAdapter()
//        {
//            @Override
//            public void onAnimationEnd(Animator animation)
//            {
//                // done
//                animacaoValorRepassesSociais(oldValueFinal, calculoFinal);
//            }
//        });
//        animator.setDuration(1000);
//        animator.setStartDelay(400);
//        animator.start();
//
//    }

    private void layoutSideMenu() {

        final ExpandableListView expandableListView;
        final ExpandableListAdapter expandableListAdapter;
        final List<String> expandableListTitle;
        final HashMap<String, List<String>> expandableListDetail;

        expandableListView = (ExpandableListView) findViewById(R.id.sideMenuExpandableListView);
        expandableListView.setGroupIndicator(null);
        expandableListDetail = ExpandableListDataSideMenu.getData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new SideMenuExpandleAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
//                Toast.makeText(getApplicationContext(),
//                        expandableListTitle.get(groupPosition) + " List Expanded.",
//                        Toast.LENGTH_SHORT).show();
                AbrirMenusGroup(groupPosition);
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
//                Toast.makeText(getApplicationContext(),
//                        expandableListTitle.get(groupPosition) + " List Collapsed.",
//                        Toast.LENGTH_SHORT).show();
                AbrirMenusGroup(groupPosition);
            }

        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
//                Toast.makeText(
//                        getApplicationContext(),
//                        expandableListTitle.get(groupPosition)
//                                + " -> "
//                                + expandableListDetail.get(
//                                expandableListTitle.get(groupPosition)).get(
//                                childPosition), Toast.LENGTH_SHORT
//
//                ).show();
//
                AbrirMenusItens(childPosition);
                return false;
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ZXING_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AnimacaoFecharMenuRapido(relativePrincipal);
                    startActivity(new Intent(PrincipalActivity.this, LerBilhetesActivity.class));
                } else {
                    Toast.makeText(this, "Por favor conceda a permissão da câmera para usar o leitor de bilhetes.", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    @Override
    protected void onDestroy() {
        dbLoteriasHelper.close();
        super.onDestroy();
    }
}
