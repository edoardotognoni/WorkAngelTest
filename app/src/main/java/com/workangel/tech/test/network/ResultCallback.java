package com.workangel.tech.test.network;

/**
 * Callback called by our Network Manager when the network called as ended
 */
public interface ResultCallback<T> {

    /**
     * Calle dif the net call has been successfull
     * @param result The response already parsed as bean
     */
    public void onSuccess(T result);

    /**
     * Some error occured
     * @param errorCode Error code
     */
    public void onError(int errorCode);
}
