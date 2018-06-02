package de.ameyering.wgplaner.wgplaner.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;
import java.util.UUID;

import io.swagger.client.ApiResponse;
import io.swagger.client.model.Group;
import io.swagger.client.model.ListItem;
import io.swagger.client.model.ShoppingList;
import io.swagger.client.model.SuccessResponse;
import io.swagger.client.model.User;

public abstract class DataProviderInterface {

    public static DataProviderInterface getInstance() {
        return new DataProvider(
                ServerCallsInterface.getInstance(),
                ImageStore.getInstance()
            );
    }

    public static DataProviderInterface getInstance(ServerCallsInterface serverCallsInterface,
        ImageStore imageStore) {
        return new DataProvider(
                serverCallsInterface,
                imageStore
            );
    }

    public enum SetUpState {
        UNREGISTERED, REGISTERED, SETUP_COMPLETED, GET_USER_FAILED, GET_GROUP_FAILED, CONNECTION_FAILED
    }

    public enum DataType {
        CURRENT_USER, CURRENT_GROUP, SHOPPING_LIST, SELECTED_ITEMS, CURRENT_GROUP_MEMBERS, BOUGHT_ITEMS
    }



    public abstract SetUpState initialize(String uid, Context context);

    public abstract void registerUser(final OnAsyncCallListener<User> listener);

    public abstract void setFirebaseInstanceId(String token, Context context);

    public abstract String getFirebaseInstanceId();

    public abstract void setCurrentUserDisplayName(String displayName,
        final OnAsyncCallListener<User> listener);

    public abstract void setCurrentUserImage(Bitmap bitmap,
        final OnAsyncCallListener<SuccessResponse> listener);

    public abstract void setCurrentUserEmail(@Nullable String email,
        final OnAsyncCallListener<User> listener);

    public abstract void setCurrentUserLocale(Locale locale,
        final OnAsyncCallListener<User> listener);

    public abstract String getCurrentUserUid();

    public abstract String getCurrentUserDisplayName();

    public abstract Bitmap getCurrentUserImage(Context context);

    public abstract String getCurrentUserEmail();

    public abstract Locale getCurrentUserLocale();

    public abstract void setCurrentGroupName(String groupName,
        final OnAsyncCallListener<Group> listener);

    public abstract void setCurrentGroupCurrency(Currency currency,
        final OnAsyncCallListener<Group> listener);

    public abstract void setCurrentGroupImage(Bitmap bitmap,
        final OnAsyncCallListener<SuccessResponse> listener);

    public abstract UUID getCurrentGroupUID();

    public abstract String getCurrentGroupName();

    public abstract Currency getCurrentGroupCurrency();

    public abstract Bitmap getCurrentGroupImage(Context context);

    public abstract ArrayList<User> getCurrentGroupMembers();

    public abstract User getUserByUid(String uid);

    public abstract boolean isAdmin(String uid);

    public abstract void createGroup(String name, String groupCountry, Bitmap image, Context context,
        final OnAsyncCallListener<Group> listener);

    public abstract void updateGroup(Group group, final OnAsyncCallListener<Group> listener);

    public abstract void joinCurrentGroup(String accessKey, Context context,
        final OnAsyncCallListener<Group> listener);

    public abstract void leaveCurrentGroup(final OnAsyncCallListener<SuccessResponse> listener);

    public abstract String createGroupAccessKey();

    public abstract void addShoppingListItem(ListItem item,
        final OnAsyncCallListener<ListItem> listener);

    public abstract void selectShoppingListItem(ListItem item);

    public abstract void unselectShoppingListItem(ListItem item);

    public abstract boolean isItemSelected(ListItem item);

    public abstract void addPriceToListItem(ListItem item, String price,
        final OnAsyncCallListener<ListItem> listener);

    public abstract void buySelection(final OnAsyncCallListener<SuccessResponse> listener);

    public abstract ListItem getListItem(UUID uuid);

    public abstract ArrayList<ListItem> getCurrentShoppingList();

    public abstract boolean isSomethingSelected();

    public abstract ArrayList<ListItem> getBoughtItems();

    public abstract ApiResponse<ShoppingList> syncShoppingList();

    public abstract void syncGroup();

    public abstract void syncGroupMembers();

    public abstract void syncGroupNewMember(String uid, Context context);

    public abstract void syncGroupMemberLeft(String uid, Context context);

    public abstract void syncGroupMember(String uid);

    public abstract void syncGroupMemberPicture(String uid, Context context);

    public abstract void syncGroupPicture(Context context);

    public abstract void syncBoughtItems();

    public abstract void addOnDataChangeListener(OnDataChangeListener listener);

    public abstract void removeOnDataChangeListener(OnDataChangeListener listener);
}
