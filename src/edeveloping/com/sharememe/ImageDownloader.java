package edeveloping.com.sharememe;

import java.io.File;
import java.util.Formatter.BigDecimalLayoutForm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class ImageDownloader {

    private static final  String TAG = "ImageDownloader";
    public static final String DIRECTORY_NAME = "ShareMeme";

    private static ImageDownloader instance = null;
    private ImageLoader imageLoader;

    protected ImageDownloader(Context context) {
        // Exists only to defeat instantiation.
        configImageDownloader(context);
    }

    public static ImageDownloader getInstance(Context context) {
        if(instance == null) {
            instance = new ImageDownloader(context);
       }
        return instance;
    }

    /**
     * This constructor will configure loader object in order to display image.
     * @param context
     */
    private void configImageDownloader(Context context) {

        File cacheDir = StorageUtils.getOwnCacheDirectory(context, DIRECTORY_NAME + "/Cache");

        // Get singleton instance of ImageLoader
        imageLoader = ImageLoader.getInstance();

        // Create configuration for ImageLoader (all options are optional)
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
        		.memoryCacheSize(1048576 * 10)
                .discCache(new UnlimitedDiscCache(cacheDir))
               
                .discCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .defaultDisplayImageOptions(
                        new DisplayImageOptions.Builder()
                                .showStubImage(R.drawable.ic_launcher)
                                .resetViewBeforeLoading()
                               // .cacheInMemory()
                                .cacheOnDisc()
                                .bitmapConfig(Bitmap.Config.RGB_565)
                                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                                .build())
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .threadPoolSize(5)
                .threadPriority(Thread.MIN_PRIORITY + 3)
                .denyCacheImageMultipleSizesInMemory()
                .build();

        // Initialize ImageLoader with created configuration.
        imageLoader.init(config);
    }


    public void displayImage(final ImageView imageView, String imageURI) {
        if(imageView == null  ||  imageURI == null) {
            Log.e(TAG, "Either of image view or image uri is null");
            return;
        }

        // Load and display image
        imageLoader.displayImage(imageURI, imageView, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {}

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {}

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {}
        });
    }
    
    public void displayImageWithCallback(final ImageView imageView, String imageURI, ImageLoadingListener ill) {
        if(imageView == null  ||  imageURI == null) {
            Log.e(TAG, "Either of image view or image uri is null");
            return;
        }

        // Load and display image
        imageLoader.displayImage(imageURI, imageView, ill);
    }
    
    public void displayImageWithText (final ImageView imageview, String imageURI, final String text, final Context mContext){
    	if(imageview == null  ||  imageURI == null) {
            Log.e(TAG, "Either of image view or image uri is null");
            return;
        }
    	
    	if (imageURI == "done"){
    		Log.d("ImageDownloader", "found a 'done'");
    		imageview.setVisibility(View.GONE);
    	}else{

        // Load and display image
        imageLoader.displayImage(imageURI, imageview, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {}

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {view.setVisibility(view.GONE);}

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                Bitmap bitmap = ((BitmapDrawable) imageview.getDrawable()).getBitmap();
                imageview.setImageBitmap(putTextOnBitmap(bitmap, text,mContext));
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {}
        });
    	}
    }
    
	public static Bitmap putTextOnBitmap (Bitmap bm, String textToOverlay, Context mContext){
    	Bitmap output = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.RGB_565);
    	
    	Resources resources = mContext.getResources();
    	float scale = resources.getDisplayMetrics().density;
    	
    	
    	Canvas c = new Canvas(output);
    	Paint p = new Paint();
    	final Rect rect = new Rect(0, 0, bm.getWidth(), bm.getHeight());
    	
    	p.setAntiAlias(true);
    	
    	p.setColor(Color.WHITE);
    	p.setTextSize(30 * scale);
    	p.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
        p.setStyle(Style.FILL);
    	
    	Rect bounds = new Rect();
        p.setTextAlign(Align.CENTER);

        p.getTextBounds(textToOverlay, 0, textToOverlay.length(), bounds);
        int x = (bm.getWidth() - bounds.width())/2;
        int y = (bm.getHeight() + bounds.height())/2; 

        
       
      
        c.drawBitmap(bm, rect, rect, p);
      
        p.setAlpha(100);
        
        c.drawRect(0,(y* scale)+10 , bm.getWidth(), (y* scale)-50, p); // white background for the text
    	
        p.setColor(Color.BLACK);
        
        c.drawText(textToOverlay, x * scale, y * scale, p);// the text
    	//c.drawText(textToOverlay, bm.getWidth(), bm.getHeight(), p);
    	
    	
    	return output;   	
    	
    }
    
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}