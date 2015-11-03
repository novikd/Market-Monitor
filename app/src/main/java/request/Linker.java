package request;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public final class Linker {

    //This is sandbox App_id. We should change it to production App_id before release
    private static final String APP_ID = "DmitryNo-a46d-46a8-b0ba-755c3fec79cc";

    private static final String BASE_FIND_URL = "http://svcs.ebay.com/services/search/FindingService/v1";
    private static final String BASE_SHOP_URL = "http://open.api.ebay.com/shopping";
    //Consts for FIND method
    private static final String PARAM_METHOD = "OPERATION-NAME";
    private static final String PARAM_VERSION = "SERVICE-VERSION";
    private static final String PARAM_APPID = "SECURITY-APPNAME";
    private static final String PARAM_FORMAT = "RESPONSE-DATA-FORMAT";
    private static final String PARAM_KEYS = "keywords";
    //Consts for SHOP mothed
    private static final String PARAM_CALL = "callname";
    private static final String PARAM_RESPONSE = "responseencoding";
    private static final String PARAM_VER = "version";
    private static final String PARAM_ITEM = "ItemID";
    //Values
    private static final String METHOD_FIND = "findItemsByKeywords";
    private static final String METHOD_SHOP = "GetSingleItem";
    private static final String VERSION_FIND = "1.0.0";
    private static final String VERSION_SHOP = "515";
    private static final String FORMAT_JSON = "JSON";

    //URL to method which gives items which match keywords
    public static URL createFindUrl(String request) throws MalformedURLException {
        Uri uri = Uri.parse(BASE_FIND_URL).buildUpon()
                .appendQueryParameter(PARAM_METHOD, METHOD_FIND)
                .appendQueryParameter(PARAM_VERSION, VERSION_FIND)
                .appendQueryParameter(PARAM_APPID, APP_ID)
                .appendQueryParameter(PARAM_FORMAT, FORMAT_JSON)
                .appendQueryParameter(PARAM_KEYS, Uri.encode(request))
                .build();
        return new URL(uri.toString());
    }

    //URL to method which gives information about item
    public static URL createShopUrl(String itemID) throws  MalformedURLException {
        Uri uri = Uri.parse(BASE_SHOP_URL).buildUpon()
                .appendQueryParameter(PARAM_CALL, METHOD_SHOP)
                .appendQueryParameter(PARAM_RESPONSE, FORMAT_JSON)
                .appendQueryParameter(PARAM_APPID, APP_ID)
                .appendQueryParameter(PARAM_VER, VERSION_SHOP)
                .appendQueryParameter(PARAM_ITEM, itemID)
                .build();
        return new URL(uri.toString());
    }

    //TODO: To add methods for getting info about service version. May be to add more params to request.

    private Linker() {}
}
