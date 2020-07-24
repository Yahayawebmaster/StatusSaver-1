package com.collabcreation.statussaver.Modal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Common {


    /*Instagram Slider Post Type*/
    public static final String INSTA_SLIDE = "GraphSidecar";
    /*Instagram Image Post Type*/
    public static final String INSTA_IMAGE = "GraphImage";
    /*Instagram Video Post Type*/
    public static final String INSTA_VIDEO = "GraphVideo";
    /*Intent Url Type For MultiPle Urls*/
    public static final String MULTI_URL = "MultiUrls";
    /*Intent Key For Url Type*/
    public static final String URL_TYPE = "UrlType";
    /*Intent Url Type For Single Url*/
    public static final String SINGLE_URL = "SingleUrl";
    /*Name Of Sharedpref For Instant Service*/
    public static final String BG_SERVICE = "bgService";
    /*Behavior of Auto Saver*/
    public static final String BEHAVIOUR_BOTH = "both";
    public static final String BEHAVIOUR_IMAGE_ONLY = "imageOnly";
    public static final String BEHAVIOUR_VIDEO_ONLY = "videoOnly";
    /*Name Of Sharedpref For Auto Saver Service*/
    public static final String AUTO_STATUS_SAVER = "autoSaver";
    /*Media Type*/
    public static final String MEDIA_TYPE = "mediatype";
    /*Instagram Profile Type*/
    public static final String INSTA_PROFILE = "profile";
    public static final String LOGIN = "login";
    /*Constant For Bg Service*/
    public static String isBgTrue = "isTrue";
    /*Intent Key For Behavior*/
    public static final String whichBehaviour = "whichBehav";
    /*Action Download For Receiver*/
    public static final String ACTION_BGDOWNLOAD = "bgDownloadAction";
    private static String csrf;

    public static File getWhatsappDirectory() {
        return new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/WhatsApp/Media/.Statuses");
    }

    public static String getCsrf() {
        return csrf;
    }

    public static void setCsrf(String csrfString, String cookieString) {
        if (csrfString == null && cookieString == null) {
            csrf = null;
        } else if (csrfString == null) {
            Matcher matcher = Pattern.compile("csrftoken=(.*?);").matcher(cookieString);
            if (matcher.find()) {
                csrf = matcher.group(1);
            }
        } else {
            csrf = csrfString;
        }
    }

    public static File getBWhatsappDirectory() {
        return new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/WhatsApp Business/Media/.Statuses");
    }

    public static File getGBWhatsappDirectory() {
        return new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/GBWhatsApp/Media/.Statuses");
    }


    public static File getOurDirectory() {
        if (!new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/Status Saver/Whatsapp Status").exists()) {
            new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/Status Saver/Whatsapp Status").mkdir();
        }
        return new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/Status Saver/Whatsapp Status");
    }

    public static Bitmap getThumb(Status status) {
        if (status.isVideo()) {
            return ThumbnailUtils.createVideoThumbnail(status.getFile().getAbsolutePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
        } else {
            return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(status.getFile().getAbsolutePath()), 128, 128);
        }
    }

    public static void copyFile(File file, File destFile) throws IOException {
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        FileChannel source = null, destination = null;
        source = new FileInputStream(file).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        destination.transferFrom(source, 0, source.size());
        source.close();
        destination.close();

    }




    public static File getGBOurDirectory() {
        if (!new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/Status Saver/GB Status").exists()) {
            new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/Status Saver/GB Status").mkdir();
        }
        return new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/Status Saver/GB Status");
    }

    public static File getBWOurDirectory() {
        if (!new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/Status Saver/BusinessWhatsapp Status").exists()) {
            new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/Status Saver/BusinessWhatsapp Status").mkdir();
        }
        return new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/Status Saver/BusinessWhatsapp Status");
    }
}
