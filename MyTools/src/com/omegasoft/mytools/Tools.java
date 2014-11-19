package com.omegasoft.mytools;

public class Tools {
	
	
	public static final String PATTERN_USERNAME = "^[a-z0-9_-]{1,30}$";
	public static final String PATTERN_EMAIL = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";

	public static final String FONT_BOLD = "fonts/OpenSans-Bold.ttf"; 
	public static final String FONT_ITALIC = "fonts/OpenSans-Italic.ttf";
	public static final String FONT_LIGHT = "fonts/OpenSans-Light.ttf"; 
	
	public static final String FONT_REGULAR = "fonts/OpenSans-Regular.ttf";
	public static final String FONT_SEMIBOLD = "fonts/OpenSans-Semibold.ttf"; 
	public static final String FONT_SEMIBOLDITALIC = "fonts/OpenSans-SemiboldItalic.ttf"; 
	
	/**
	 * Checking for all possible Internet providers
	 * **/
	public static boolean isConnectingToInternet(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

		}
		return false;
	}
	
		/**
	 * Checking which view is visible and has effect in given activity
	 * **/
	public static void CheckVisibleViews(Activity activity) {
		RelativeLayout parent = (RelativeLayout) activity.findViewById(R.id.layoutMEDMAINmainParent);

		for (int i = 0; i < parent.getChildCount(); i++) {
			if (parent.getChildAt(i).getVisibility() == View.VISIBLE) {

				HYUtils.logOut("CheckVisibleViews Visible. name:" + activity.getResources().getResourceEntryName(parent.getChildAt(i).getId()) + " Width:"
						+ parent.getChildAt(i).getWidth());

			} else if (parent.getChildAt(i).getVisibility() == View.INVISIBLE) {

				HYUtils.logOut("CheckVisibleViews INVISIBLE. name:" + activity.getResources().getResourceEntryName(parent.getChildAt(i).getId()) + " Width:"
						+ parent.getChildAt(i).getWidth());

			}
		}
	}
	
	
	public static DisplayMetrics GetScreenSize(Activity activity) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		return displayMetrics;
	}

	public static Point GetScreenPoint(Activity activity) {
		Display display = activity.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		return size;
	}

	public static int GetFontSize(Activity activity, int fontsize) {
		
		DisplayMetrics dm = GetScreenSize(activity);
		
		final float fixedFontSize = 480f / (float)dm.densityDpi;
		
		return (int) (fixedFontSize * fontsize);
	}

	public static class DateDiff {
		public long seconds;
		public long minutes;
		public long hours;
		public long days;

		public DateDiff(long seconds, long minutes, long hours, long days) {
			this.seconds = seconds;
			this.days = days;
			this.minutes = minutes;
			this.hours = hours;
		}
	}
	
	public static ArrayList<JSONObject> JsonArray2ArrayList(JSONArray jArray) {
		ArrayList<JSONObject> result = new ArrayList<JSONObject>();
		for (int i = 0; i < jArray.length(); i++) {
			result.add(HYUtils.getJsonObject(jArray, i, new JSONObject()));
		}
		return result;
	}

	public static void logOut(String string) {

		if (string.length() > 3000) {
			int length = string.length();
			String str = string;

			while (length > 3000) {
				Log.w("Tools", str.substring(0, 3000));
				str = str.substring(3000);
				length = str.length();
			}

			Log.w("Tools", str);
		} else {
			Log.w("Tools", string);
		}
	}
	
	public static void printPackageSignature(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo("com.test.twst", PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				HYUtils.logOut("SIGNATURE: " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {
			HYUtils.logOut("SIGNATURE: NameNotFoundException");
		} catch (NoSuchAlgorithmException e) {
			HYUtils.logOut("SIGNATURE: NoSuchAlgorithmException");
		}
	}

	public static String getBuidID(Context context) {
		PackageInfo pInfo;
		try {
			pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return pInfo.versionName;
		} catch (NameNotFoundException e) {
			return "ERROR";
		}
	}
	
	public static void initLabel(Context context, TextView view, String fontName, int size, int color) {
		Typeface tf = Typeface.createFromAsset(context.getAssets(), fontName);
		view.setTypeface(tf);
		view.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
		view.setTextColor(color);
	}

	public static void initButtonFont(Context context, Button view, String fontName, int size, int color) {
		Typeface tf = Typeface.createFromAsset(context.getAssets(), fontName);
		view.setTypeface(tf);
		view.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
		view.setTextColor(color);
	}

	public static Bitmap downloadBitmap(String url) {
		final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		final HttpGet getRequest = new HttpGet(url);
		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				logOut("Error " + statusCode + " while retrieving bitmap from " + url);
				return null;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
					return bitmap;
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			getRequest.abort();
			logOut("Error while retrieving bitmap from " + url);
		} finally {
			if (client != null) {
				client.close();
			}
		}
		return null;
	}

	public static void putJsonObject(JSONObject json, String key, JSONObject data) {
		try {
			json.put(key, data);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static String getJsonString(JSONObject json, String key, String default_value) {
		if (json == null) {
			return default_value;
		}
		try {
			return json.getString(key);
		} catch (JSONException e) {
			return default_value;
		}
	}

	public static String getJsonString(JSONObject json, String[] keys, String default_value) {
		JSONObject j = json;
		String result = default_value;
		for (int i = 0; i < keys.length; i++) {
			if (j == null) {
				return result;
			}
			if (i < keys.length - 1) {
				try {
					j = j.getJSONObject(keys[i]);
				} catch (JSONException e) {
					return result;
				}
			} else {
				try {
					result = j.getString(keys[i]);
					return result;
				} catch (JSONException e) {
					return result;
				}
			}
		}
		return default_value;
	}

	public static int getJsonInt(JSONObject json, String key, int default_value) {
		if (json == null) {
			return default_value;
		}
		try {
			return json.getInt(key);
		} catch (JSONException e) {
			return default_value;
		}
	}

	public static int getJsonInt(JSONObject json, String[] keys, int default_value) {
		JSONObject j = json;
		int result = default_value;
		for (int i = 0; i < keys.length; i++) {
			if (j == null) {
				return result;
			}
			if (i < keys.length - 1) {
				try {
					j = j.getJSONObject(keys[i]);
				} catch (JSONException e) {
					return result;
				}
			} else {
				try {
					result = j.getInt(keys[i]);
					return result;
				} catch (JSONException e) {
					return result;
				}
			}
		}
		return default_value;
	}

	public static boolean getJsonBool(JSONObject json, String[] keys, boolean default_value) {
		JSONObject j = json;
		boolean result = default_value;
		for (int i = 0; i < keys.length; i++) {
			if (j == null) {
				return result;
			}
			if (i < keys.length - 1) {
				try {
					j = j.getJSONObject(keys[i]);
				} catch (JSONException e) {
					return result;
				}
			} else {
				try {
					result = j.getBoolean(keys[i]);
					return result;
				} catch (JSONException e) {
					return result;
				}
			}
		}
		return default_value;
	}

	public static boolean getJsonBool(JSONObject json, String key, boolean default_value) {
		if (json == null) {
			return default_value;
		}
		try {
			return json.getBoolean(key);
		} catch (JSONException e) {
			return default_value;
		}
	}

	public static float getJsonFloat(JSONObject json, String key, float default_value) {
		if (json == null) {
			return default_value;
		}
		try {
			return (float) json.getDouble(key);
		} catch (JSONException e) {
			return default_value;
		}
	}

	public static JSONArray getJsonArray(JSONObject json, String key, JSONArray default_value) {
		if (json == null) {
			return default_value;
		}
		try {
			return json.getJSONArray(key);
		} catch (JSONException e) {
			return default_value;
		}
	}

	public static JSONArray getJsonArray(JSONObject json, String[] keys, JSONArray default_value) {
		JSONObject j = json;
		JSONArray result = default_value;
		for (int i = 0; i < keys.length; i++) {
			if (j == null) {
				return result;
			}
			if (i < keys.length - 1) {
				try {
					j = j.getJSONObject(keys[i]);
				} catch (JSONException e) {
					return result;
				}
			} else {
				try {
					result = j.getJSONArray(keys[i]);
					return result;
				} catch (JSONException e) {
					return result;
				}
			}
		}
		return default_value;
	}

	public static JSONObject getJsonObject(JSONObject json, String key, JSONObject default_value) {
		if (json == null) {
			return default_value;
		}
		try {
			return json.getJSONObject(key);
		} catch (JSONException e) {
			return default_value;
		}
	}

	public static JSONObject getJsonObject(String data, JSONObject default_value) {
		if (data == null) {
			return default_value;
		}

		if (data.startsWith("[") && data.endsWith("]")) {
			data = data.substring(1, data.length() - 1);
		}

		try {
			return new JSONObject(data);
		} catch (JSONException e) {
			return default_value;
		}
	}

	public static JSONArray getJsonArray(String data, JSONArray default_value) {
		if (data == null) {
			return default_value;
		}
		try {
			return new JSONArray(data);
		} catch (JSONException e) {
			return default_value;
		}
	}

	public static JSONObject getJsonObject(JSONArray jsonArray, int index, JSONObject default_value) {
		if (jsonArray == null) {
			return default_value;
		}
		try {
			return jsonArray.getJSONObject(index);
		} catch (JSONException e) {
			return default_value;
		}
	}

	public static JSONArray getJsonArray(JSONArray jsonArray, int index, JSONArray default_value) {
		if (jsonArray == null) {
			return default_value;
		}
		try {
			return jsonArray.getJSONArray(index);
		} catch (JSONException e) {
			return default_value;
		}
	}

	public static int getJsonInt(JSONArray jsonArray, int index, int default_value) {
		if (jsonArray == null) {
			return default_value;
		}
		try {
			return jsonArray.getInt(index);
		} catch (JSONException e) {
			return default_value;
		}
	}

	public static String getJsonString(JSONArray jsonArray, int index, String default_value) {
		if (jsonArray == null) {
			return default_value;
		}
		try {
			return jsonArray.getString(index);
		} catch (JSONException e) {
			return default_value;
		}
	}

	public static JSONObject getJsonObject(JSONObject json, String[] keys, JSONObject default_value) {
		JSONObject j = json;
		JSONObject result = default_value;
		for (int i = 0; i < keys.length; i++) {
			if (j == null) {
				return result;
			}
			if (i < keys.length - 1) {
				try {
					j = j.getJSONObject(keys[i]);
				} catch (JSONException e) {
					return result;
				}
			} else {
				try {
					result = j.getJSONObject(keys[i]);
					return result;
				} catch (JSONException e) {
					return result;
				}
			}
		}
		return default_value;
	}

	@SuppressLint("SimpleDateFormat")
	public static Date StringToDate(String sDate, String sFormat) {
		SimpleDateFormat format = new SimpleDateFormat(sFormat);
		try {
			return format.parse(sDate);
		} catch (java.text.ParseException e) {
			return new Date();
		}
	}

	@SuppressWarnings("deprecation")
	public static long pastDaysAgo(Date date) {
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
		return Math.round((System.currentTimeMillis() - date.getTime()) / 86400000D) - 1;
	}

	public static DateDiff DiffBetweenDates(Date date1, Date date2) {
		long diff = date2.getTime() - date1.getTime();
		long seconds = diff / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		long days = hours / 24;
		return new DateDiff(seconds, minutes, hours, days);
	}

	public static int colorFromIOS(double r, double g, double b, double a) {
		int red = (int) (r * 255);
		int green = (int) (g * 255);
		int blue = (int) (b * 255);
		int alpha = (int) (a * 255);
		return Color.argb(alpha, red, green, blue);
	}
	
	public static boolean isValidEmailAddress(String email) {
		Pattern pattern = Pattern.compile(PATTERN_EMAIL);
		return pattern.matcher(email).matches();
	}

	public static boolean isValidUsername(String username) {
		Pattern pattern = Pattern.compile(PATTERN_USERNAME);
		return pattern.matcher(username).matches();
	}
	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int borderColor, float roundPx, int borderWidth) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		if (roundPx < 0) {
			roundPx = bitmap.getWidth() / 2;
		}

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		if (borderColor != -1) {
			paint.setColor(borderColor);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(borderWidth);
			int r = (int) roundPx;
			canvas.drawCircle(r, r, r, paint);
		}

		return output;
	}

	public static void msgBoxOK(Context context, String title, String text) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setMessage(text).setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// frmLogin.this.finish();
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	// this will calculate and return dp value by getting pixel value
	public static int getDPbyPicxel(int pix) {
		return (int) (pix * HYGlobal.density + 0.5f);
	}
	
	// Make seconds to date format MM:SS
	public static String MakeTime(int time) {
		int min = time / 60;
		int sec = time - (min * 60);
		return (min < 10 ? "0" + min : min) + ":" + (sec < 10 ? "0" + sec : sec);
	}

	// turn html text to text
	// It used for change html values received from json
	public static String Html2String(String html) {
		html = html.replace("<p>", "");
		html = html.replace("</p>", "");
		html = html.replace("<div>", "");
		html = html.replace("</div>", "");
		html = html.replace("<br>", "");
		html = html.replace("<br />", "");
		return html;
	}
	
	// This method receive a hex color code without # and will return an int
	// array for rgb codes
	// 0 -> r 1-> g 2-> b
	public static int[] HexToRGB(String hexColor) {
		int[] rgb = new int[3];

		int color = (int) Long.parseLong(hexColor, 16);
		rgb[0] = (color >> 16) & 0xFF;
		rgb[1] = (color >> 8) & 0xFF;
		rgb[2] = (color >> 0) & 0xFF;

		return rgb;
	}
	
	public static void animShowGoneView(final View view, final boolean hide, int length) {

		// if (view.getVisibility() == (hide ? View.GONE : View.VISIBLE)) {
		// return;
		// }

		if (length == 0) {
			view.setVisibility(hide ? View.GONE : View.VISIBLE);
		} else {

			AlphaAnimation anim = new AlphaAnimation(hide ? 1 : 0, hide ? 0 : 1);
			anim.setDuration(length);
			// anim.setFillAfter(true);
			view.startAnimation(anim);

			if (!hide) {
				view.setVisibility(View.VISIBLE);
			}

			anim.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					if (hide) {
						view.setVisibility(View.GONE);
					}
				}
			});
		}
	}

	public static void animScaleView(final View view, float fromScale, float toScale, int length) {

		int pivotX = view.getLayoutParams().width / 2;
		int pivotY = view.getLayoutParams().height / 2;

		ScaleAnimation scale = new ScaleAnimation(fromScale, toScale, fromScale, toScale, pivotX, pivotY);
		scale.setFillAfter(true);
		scale.setDuration(length);
		view.startAnimation(scale);
	}

	public static void animShowHideView(final View view, final boolean hide, int length) {

		if (view.getVisibility() == (hide ? View.INVISIBLE : View.VISIBLE)) {
			return;
		}

		if (length == 0) {
			view.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
		} else {

			AlphaAnimation anim = new AlphaAnimation(hide ? 1 : 0, hide ? 0 : 1);
			anim.setDuration(length);
			// anim.setFillAfter(true);
			view.startAnimation(anim);

			if (!hide) {
				view.setVisibility(View.VISIBLE);
			}

			anim.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					if (hide) {
						view.setVisibility(View.INVISIBLE);
					}
				}
			});
		}
	}

	public static void htmlToTextView(TextView view, String htmlString) {

		htmlString = htmlString.trim();

		if (htmlString.startsWith("<p>")) {
			htmlString = htmlString.substring(3);
		}

		htmlString = htmlString.replace("<sup>", "").replace("</sup>", "");
		htmlString = htmlString.replace("target=\"_blank\"", "");
		htmlString = htmlString.replace("<a href=\"/", "<a href=\"http://my.happify.com/");
		htmlString = htmlString.replace("<p>", "<br><br>");
		htmlString = htmlString.replace("</p>", "");
		htmlString = htmlString.replace("<a", "<font color = #f16c39><a").replace("</a>", "</a></font>");

		view.setText(Html.fromHtml(htmlString));
		view.setMovementMethod(LinkMovementMethod.getInstance());
	}

	public static void htmlToTextViewWithParagraphs(TextView view, String htmlString) {
		htmlString = htmlString.replace("<sup>", "").replace("</sup>", "").replace("target=\"_blank\"", "").replace("<a href=\"/", "<a href=\"http://my.happify.com/");
		view.setText(Html.fromHtml(htmlString));
		view.setMovementMethod(LinkMovementMethod.getInstance());
	}
}
