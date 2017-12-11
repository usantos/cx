package caixa.gov.br.loterias.view;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import caixa.gov.br.loterias.R;

/**
 * Created by joafilho on 06/12/2017.
 */

public class CadastroActivity extends AppCompatActivity {

    protected void createToobar(){
        setTitle(R.string.title_activity_toolbar);


        if (getSupportActionBar() != null){

            ImageView imageView = new ImageView(getSupportActionBar().getThemedContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            ActionBar.LayoutParams paramsImage = new ActionBar.LayoutParams(
                    48,
                    48, Gravity.RIGHT
                    | Gravity.CENTER_VERTICAL);
            paramsImage.rightMargin = 40;
            imageView.setLayoutParams(paramsImage);
            imageView.setBackgroundResource(R.drawable.icon_loteria);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setCustomView(imageView);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable((R.drawable.background_dialogtitle_gradient_color)));
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();

        return true;
    }
}
