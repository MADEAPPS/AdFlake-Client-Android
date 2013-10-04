Please see http://www.adflake.com/SDK for setup instructions, 
the latest news, releases and issue reports.

===== Quick Start =====

Add the following mandatory permissions to AndroidManifest.xml

	<uses-permission android:name="android.permission.INTERNET" />

And the following optional permissions

	<uses-permission android:name="android.permission.READ_PHONE_STATE" />
	<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" /> 
	<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" /> 

Create an AdFlakeLayout. Make sure that this code is called on the main thread.

	AdFlakeLayout adFlakeLayout = new AdFlakeLayout(this, "***YOUR AdFlake.com SDK KEY***");
	RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(320, 52);
	layout.addView(adFlakeLayout, layoutParams);

You can choose whatever size you want for the layout. 
We suggest 320x52, as this is the largest ad unit size. If you choose to make the view smaller, 
ads will still display, but may cut off a few pixels and display a small scroll bar.

===== Sample Application =====

Check out our SampleActivity inside the SDK eclipse project.
Import the SDK project into your Eclipse (ADT) workspace and build the application.
 
