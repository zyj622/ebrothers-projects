package com.ebrothers.forestrunner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class CustomAlertDialog extends AlertDialog {
	protected CustomAlertDialog(Context context) {
		super(context, R.style.AlertDialogTheme);
	}

	public static void showExitConfirmDialog(Context context,
			OnClickListener onOkClicked) {
		final CustomAlertDialog dialog = new CustomAlertDialog(context);
		dialog.setTitle("Confirm");
		dialog.setMessage("Are you sure you want to quit the game?");
		dialog.setButton(BUTTON_POSITIVE,
				context.getString(android.R.string.yes), onOkClicked);
		dialog.setButton(BUTTON_NEGATIVE,
				context.getString(android.R.string.no), new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		dialog.show();
	}
	
	public static void showRatingDialog(Context context,
			OnClickListener onRateClicked,OnClickListener onShareClicked){
		final CustomAlertDialog dialog = new CustomAlertDialog(context);
		dialog.setTitle("Like it !");
		
		dialog.setMessage("Do you like our game? Please help \nrate and share if you like it!");
		dialog.setButton(BUTTON_POSITIVE,
				"Rate", onRateClicked);
		dialog.setButton(BUTTON_NEUTRAL,
				"Share", onShareClicked);
		dialog.setButton(BUTTON_NEGATIVE,
				"Not Now", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		dialog.show();
	}
}
