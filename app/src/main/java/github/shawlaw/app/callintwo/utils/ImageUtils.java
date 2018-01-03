package github.shawlaw.app.callintwo.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;

import github.shawlaw.app.callintwo.repository.Contacts;
import github.shawlaw.app.callintwo.repository.bean.SelectedContactBean;

/**
 * 图片加载工具
 * @author Shawlaw
 * @date 2017/11/28
 */

public class ImageUtils {

    private static final int LOAD_URI_TAG_KEY = 0x12341234;
    private static WorkerUtils sWorker;

    static {
        sWorker = WorkerUtils.getInstance();
    }

    public static void loadImage(final ImageView targetView, final String imgPath) {
        if (imgPath == null) {
            return;
        }
        targetView.setTag(LOAD_URI_TAG_KEY, imgPath);
        final WeakReference<ImageView> weakRef = new WeakReference<>(targetView);
        sWorker.submit(new Runnable() {
            @Override
            public void run() {
                final ImageView targetRef = weakRef.get();
                if (targetRef == null) {
                    return;
                }
                int width = targetRef.getWidth();
                int height = targetRef.getHeight();
                if (width == 0 || height == 0) {
                    sWorker.submit(this);
                    return;
                }
                Context context = targetRef.getContext();
                Bitmap img = null;
                try {
                    img = loadImgFromUriImpl(context, width, height, imgPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (img != null) {
                    String currentUri = (String) targetRef.getTag(LOAD_URI_TAG_KEY);
                    if (imgPath.equals(currentUri)) {
                        final Bitmap finalImg = img;
                        targetRef.post(new Runnable() {
                            @Override
                            public void run() {
                                targetRef.setImageBitmap(finalImg);
                            }
                        });
                    }
                }
            }
        });
    }

    private static Bitmap loadImgFromUriImpl(
            Context context, int width, int height, String imgPath)
            throws IOException {
        ContentResolver resolver = context.getContentResolver();
        Bitmap img = null;
        AssetFileDescriptor imgFD = null;
        try {
            Uri targetUri = Uri.parse(imgPath);
            imgFD = resolver.openAssetFileDescriptor(targetUri, "r");
            if (imgFD == null) {
                return null;
            }
            Rect rect = new Rect(0, 0, 0, 0);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(imgFD.getFileDescriptor(), rect, options);
            options.inJustDecodeBounds = false;
            boolean needScale = (options.outWidth > width || options.outHeight > height);
            if (needScale) {
                options.inSampleSize = Math.max(
                        Math.round(options.outWidth * 1f / width),
                        Math.round(options.outHeight * 1f / height));
                img = BitmapFactory.decodeFileDescriptor(imgFD.getFileDescriptor(), rect, options);
            } else {
                img = BitmapFactory.decodeFileDescriptor(imgFD.getFileDescriptor());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (imgFD != null) {
                try {
                    imgFD.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return img;
    }

    public static void loadImageToNotification(
            Context context, final int width, final int height, final SelectedContactBean bean) {
        if (context == null || bean == null || bean.getAvatarPath() == null) {
            return;
        }
        final Context appContext = context.getApplicationContext();
        sWorker.submit(new Runnable() {
            @Override
            public void run() {
                Bitmap img = null;
                try {
                    img = loadImgFromUriImpl(appContext, width, height, bean.getAvatarPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (img != null) {
                    if (Contacts.getInstance().getById(bean.getNotificationId()) != null) {
                        bean.setAvatarBm(img);
                        NotificationUtils.sendNotification(appContext, bean);
                    }
                }
            }
        });
    }
}
