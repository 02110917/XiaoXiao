package com.flying.xiao.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class ShowDialogUtil
{
	public static void showDialog(final Activity context)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("��ʾ");
		builder.setMessage("����δ����,�Ƿ��˳�?");
		builder.setPositiveButton("�˳�", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				context.finish();
			}
		});
		builder.setNeutralButton("ȡ��", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				dialog.dismiss();
			}
		});
		builder.show();
	}

}
