package com.flying.xiao.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class ShowDialogUtil
{
	public static void showDialog(final Activity context)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("提示");
		builder.setMessage("您还未保存,是否退出?");
		builder.setPositiveButton("退出", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				context.finish();
			}
		});
		builder.setNeutralButton("取消", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				dialog.dismiss();
			}
		});
		builder.show();
	}

}
