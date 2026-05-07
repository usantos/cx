package br.gov.caixa.loterias.apostas.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogDoisBotoesListener;

public class RateUtils {
	public static void solicitarReview(Activity activity) {
		ReviewManager manager = ReviewManagerFactory.create(activity);

		Task<ReviewInfo> request = manager.requestReviewFlow();

		request.addOnCompleteListener(task -> {
			if (task.isSuccessful()) {

				ReviewInfo reviewInfo = task.getResult();

				manager.launchReviewFlow(activity, reviewInfo)
						.addOnCompleteListener(flow -> {
							// sempre seguir fluxo normal
						});

			} else {
				// fallback opcional → abrir Play Store
			}
		});
	}

	public static void redirecionaParaLoja(Activity activity){
		final String appPackageName = activity.getPackageName();
		try {
			activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(activity.getResources().getString(R.string.google_url_market) + appPackageName)));
		} catch (android.content.ActivityNotFoundException anfe) {
			activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(activity.getResources().getString(R.string.google_url_playstore) + appPackageName)));
		}
	}

}