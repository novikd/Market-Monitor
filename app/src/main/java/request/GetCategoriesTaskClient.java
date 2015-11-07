package request;

import java.util.List;

import target.Category;

/**
 * Created by ruslanthakohov on 07/11/15.
 */
public interface GetCategoriesTaskClient {
    void categoriesAreReady(List<Category> categories);
    void downloadFailed();
}
