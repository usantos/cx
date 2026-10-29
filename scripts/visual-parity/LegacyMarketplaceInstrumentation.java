package br.gov.caixa.parity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.json.*;
import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Controlador só para teste: Activities, leiautes, holders e renderização reais da CX instalada ficam intactos. */
public class LegacyMarketplaceInstrumentation extends Instrumentation {
    private static final String ROOT = "br.gov.caixa.loterias.apostas.";
    private Bundle args;
    private JSONObject fixture;
    private ClassLoader loader;
    private Activity activity;
    private volatile Activity resumedActivity;
    @Override public void callActivityOnResume(Activity value) {super.callActivityOnResume(value);resumedActivity=value;}
    private String prefix;
    @Override public void onCreate(Bundle args) { super.onCreate(args); this.args=args; start(); }
    private Class<?> type(String name) throws Exception { return loader.loadClass(name.startsWith(".") ? ROOT+name.substring(1) : name); }
    private Object call(Object target, String method, Object... args) throws Exception {
        Class<?> cls=target instanceof Class ? (Class<?>)target : target.getClass();
        for(Method m:cls.getMethods()) if(m.getName().equals(method)&&m.getParameterTypes().length==args.length) {
            boolean match=true; Class<?>[] ts=m.getParameterTypes();
            for(int i=0;i<args.length;i++) if(args[i]!=null&&!ts[i].isInstance(args[i])&&!ts[i].isPrimitive()) match=false;
            if(match) return m.invoke(target instanceof Class ? null:target,args);
        }
        throw new NoSuchMethodException(cls+"."+method);
    }
    private void staticField(String cls,String name,Object value) throws Exception { Field f=type(cls).getDeclaredField(name);f.setAccessible(true);f.set(null,value); }
    private Object decode(String key,String className) throws Exception {
        Object gson=type("com.google.gson.Gson").getConstructor().newInstance();
        return call(gson,"fromJson",fixture.get(key).toString(),type(className));
    }
    private void seed() throws Exception {
        String json;
        try(InputStream in=getContext().getAssets().open("marketplace-parity-fixture.json")) { ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] b=new byte[8192];int n;while((n=in.read(b))!=-1)out.write(b,0,n);json=out.toString("UTF-8"); }
        fixture=new JSONObject(json);
        fixture.put("cart",new JSONObject("{\"apostas\":[{\"valor\":35}],\"valorTotal\":35}"));
        call(call(type(".model.bo.silce.internals.CarrinhoSingleton"),"getInstance"),"setCarrinho",decode("cart",".model.bo.silce.dto.CarrinhoDTO"));
        Object session=call(type(".model.bean.SessaoUsuario"),"getInstance");
        call(session,"setParametrosSimulacao",decode("parameters",".model.bo.silce.dto.ParametrosSimulacao"));
        call(session,"setDataUltimaRequisicao",new Date());
        boolean october=Boolean.parseBoolean(args.getString("october","true"));
        Class<?> prefs=type(".utils.SharedPreferencesUtils");
        call(prefs,"setValor","outubroRosa",october);
        call(prefs,"setValor","outubroRosaMegaSena",october);
        call(prefs,"setValor","qtdeMinimaApostaBolaoFiltro",1);
        call(prefs,"setValor","qtdeMaximaApostaBolaoFiltro",15);
        staticField(".utils.AppState","wasInBackground",false);
        installNetwork();
    }
    private void installNetwork() throws Exception {
        Class<?> networkType=type("com.android.volley.Network");
        java.util.concurrent.atomic.AtomicInteger poolRequests=new java.util.concurrent.atomic.AtomicInteger();
        Object network=Proxy.newProxyInstance(loader,new Class[]{networkType},(proxy,method,params)->{
            if(!method.getName().equals("performRequest")) return null;
            String url=(String)call(params[0],"getUrl");
            String encoded=android.net.Uri.parse(url).getPath().replaceAll("/+$", "");
            encoded=encoded.substring(encoded.lastIndexOf('/')+1);
            try { url=new String(android.util.Base64.decode(encoded,android.util.Base64.DEFAULT),StandardCharsets.UTF_8); } catch(IllegalArgumentException ignored) {}
            String key=url.contains("recuperar-boloes")?"pools":url.contains("loterica-favorita")?"retailers":url.contains("parametros")?"parameters": "profile";
            if(url.contains("incluir")||url.contains("excluir")||url.contains("pagamento")||url.contains("reservar")) throw new IllegalStateException("Mutation forbidden in visual fixture");
            Object payload=fixture.get(key);
            if(key.equals("pools") && poolRequests.getAndIncrement()>0)payload=new JSONObject("{\"totalRegistros\":0,\"paginaAtual\":1,\"ultimaPagina\":0,\"cotas\":[]}");
            byte[] bytes=("{\"payload\":"+payload+"}").getBytes(StandardCharsets.UTF_8);
            Map<String,String> headers=new HashMap<>(); headers.put("Content-Type","application/json; charset=utf-8");
            return type("com.android.volley.NetworkResponse").getConstructor(int.class,byte[].class,Map.class,boolean.class).newInstance(200,bytes,headers,false);
        });
        Object cache=type("com.android.volley.toolbox.NoCache").getConstructor().newInstance();
        Object queue=type("com.android.volley.RequestQueue").getConstructor(type("com.android.volley.Cache"),networkType).newInstance(cache,network);
        for(String cls:new String[]{"SilceSingleton","NovaAPISingleton","MicroServicoSingleton","ApostadorSingleton","BffSingleton","NuvemSingleton"}) {
            Object singleton=call(type(".model.bo.silce.internals."+cls),"getInstance");
            call(call(singleton,"getRequestQueue"),"stop");
            Field f=singleton.getClass().getDeclaredField("queue");f.setAccessible(true);f.set(singleton,queue);
        }
        call(queue,"start");
    }
    private int id(String name) {return getTargetContext().getResources().getIdentifier(name,"id",getTargetContext().getPackageName());}
    private void click(String name) {runOnMainSync(()->{View v=activity.findViewById(id(name));if(v==null)throw new IllegalStateException("Missing view: "+name);v.performClick();});waitForIdleSync();SystemClock.sleep(300);}
    private void write(String name,byte[] bytes) throws Exception {try(FileOutputStream out=new FileOutputStream(new File(getTargetContext().getFilesDir(),name))){out.write(bytes);}}
    private void walk(View v,JSONArray nodes) throws Exception {
        JSONObject n=new JSONObject();Rect r=new Rect();v.getGlobalVisibleRect(r);
        n.put("class",v.getClass().getName());n.put("id",v.getId()==View.NO_ID?"":v.getResources().getResourceEntryName(v.getId()));
        n.put("bounds",new JSONArray(new int[]{r.left,r.top,r.right,r.bottom}));n.put("visibility",v.getVisibility());
        if(v instanceof TextView) {TextView t=(TextView)v;n.put("text",t.getText().toString());n.put("textSizePx",t.getTextSize());n.put("color",t.getCurrentTextColor());}
        nodes.put(n);if(v instanceof ViewGroup)for(int i=0;i<((ViewGroup)v).getChildCount();i++)walk(((ViewGroup)v).getChildAt(i),nodes);
    }
    private View findText(View view,String text) {
        if(view instanceof TextView && text.equals(((TextView)view).getText().toString()))return view;
        if(view instanceof ViewGroup)for(int i=0;i<((ViewGroup)view).getChildCount();i++){
            View match=findText(((ViewGroup)view).getChildAt(i),text);if(match!=null)return match;
        }
        return null;
    }
    private void clickText(String text) {
        runOnMainSync(()->{
            View view=findText(resumedActivity.getWindow().getDecorView(),text);
            if(view==null)throw new IllegalStateException("Missing window text: "+text);
            if(!view.performClick())throw new IllegalStateException("Cannot click: "+text);
        });waitForIdleSync();SystemClock.sleep(450);
    }
    private void capture(String state) throws Exception {
        runOnMainSync(()->{try{
            Object manager=call(resumedActivity,"getSupportFragmentManager");
            for(Object fragment:(java.util.List<?>)call(manager,"getFragments"))
                if(fragment.getClass().getName().endsWith("SomadorCarrinhoFragment"))call(fragment,"atualizaValorTotal",decode("cart",".model.bo.silce.dto.CarrinhoDTO"));
        }catch(Exception e){throw new RuntimeException(e);}});
        waitForIdleSync();SystemClock.sleep(450);
        Bitmap image=getUiAutomation().takeScreenshot();if(image==null)throw new IllegalStateException("No screenshot");
        ByteArrayOutputStream png=new ByteArrayOutputStream();image.compress(Bitmap.CompressFormat.PNG,100,png);write(prefix+"-"+state+".png",png.toByteArray());
        JSONArray nodes=new JSONArray();runOnMainSync(()->{try{walk(resumedActivity.getWindow().getDecorView(),nodes);}catch(Exception e){throw new RuntimeException(e);}});
        write(prefix+"-"+state+".json",nodes.toString(2).getBytes(StandardCharsets.UTF_8));
    }
    @Override public void onStart() {
        Bundle result=new Bundle();
        try {
            loader=getTargetContext().getClassLoader();seed();
            String screen=args.getString("screen","filter");prefix="legacy-"+screen+(Boolean.parseBoolean(args.getString("october","true"))?"-october":"-normal");
            String cls=screen.equals("tutorial")?ROOT+"view.activity.TutorialActivity":screen.equals("filter")?ROOT+"controllers.FiltraMarketplaceActivity":ROOT+"view.activity.BolaoActivity";
            Intent intent=new Intent(Intent.ACTION_MAIN).setClassName(getTargetContext().getPackageName(),cls).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(screen.equals("tutorial")) {
                String key=getTargetContext().getString(getTargetContext().getResources().getIdentifier("tipoAposta","string",getTargetContext().getPackageName()));
                intent.putExtra(key,(Serializable)Enum.valueOf((Class)type(".model.bo.silce.dto.ModalidadeEnum"),"BOLAO"));
                intent.putExtra("ARG_IDMODALIDADE",2);intent.putExtra("ARG_TIPO_CONCURSO",1);
            } else if(screen.equals("filter")) {
                Object filter=type(".model.bean.FiltroAplicadoMarketplace").getConstructor().newInstance();
                call(filter,"setModalidade",Enum.valueOf((Class)type(".model.bo.silce.dto.ModalidadeEnum"),"MEGA_SENA"));
                call(filter,"setTipoConcurso",Enum.valueOf((Class)type(".model.bo.silce.dto.TipoConcursoEnum"),"NORMAL"));
                intent.putExtra("ARG_FILTRO_MARKETPLACE",(Serializable)filter);
                intent.putExtra("ARG_FILTRO_LIMPO",(Serializable)filter);
            } else { intent.putExtra("ARG_IDMODALIDADE",Boolean.parseBoolean(args.getString("modality","false"))?2:0);intent.putExtra("ARG_TIPO_CONCURSO",1); }
            activity=startActivitySync(intent);waitForIdleSync();SystemClock.sleep(1500);
            capture("initial");
            if(screen.equals("tutorial")) {
                click("tv_pular");SystemClock.sleep(5000);capture("final");
            } else if(screen.equals("filter")) {
                runOnMainSync(()->((android.widget.ScrollView)activity.findViewById(id("scroll_filtra_market"))).fullScroll(View.FOCUS_DOWN));
                capture("bottom");
                click("img_accordion_qtd_dezenas");capture("quantity-dialog");
                clickText("06");capture("quantity-selected");clickText("Confirmar");
                runOnMainSync(()->((android.widget.ScrollView)activity.findViewById(id("scroll_filtra_market"))).fullScroll(View.FOCUS_DOWN));capture("selection");
            } else {
                runOnMainSync(()->activity.findViewById(id("rv_conteudo_boloes")).scrollBy(0,1600));
                capture("list");click("seta_bolao");
                runOnMainSync(()->activity.findViewById(id("rv_conteudo_boloes")).scrollBy(0,1600));capture("expanded");
                click("btn_lotericas_favoritas");capture("favorites");
            }
            result.putString("files",prefix+"-initial.png,json");finish(Activity.RESULT_OK,result);
        } catch(Throwable error) {StringWriter s=new StringWriter();error.printStackTrace(new PrintWriter(s));result.putString("error",s.toString());finish(Activity.RESULT_CANCELED,result);}
    }
}
