package com.example.portalnexus.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import org.junit.Before;
import org.junit.Test;

public class NetworkUtilsTest {

    private Context mockContext;
    private ConnectivityManager mockConnectivityManager;
    private NetworkCapabilities mockCapabilities;

    @Before
    public void setUp() {
        mockContext = mock(Context.class);
        mockConnectivityManager = mock(ConnectivityManager.class);
        mockCapabilities = mock(NetworkCapabilities.class);

        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager);
    }

    @Test
    public void isNetworkAvailable_nullContext_returnsFalse() {
        assertFalse(NetworkUtils.isNetworkAvailable(null));
    }

    @Test
    public void isNetworkAvailable_hasWifi_returnsTrue() {
        Network mockNetwork = mock(Network.class);
        when(mockConnectivityManager.getActiveNetwork()).thenReturn(mockNetwork);
        when(mockConnectivityManager.getNetworkCapabilities(mockNetwork)).thenReturn(mockCapabilities);
        when(mockCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(true);

        assertTrue(NetworkUtils.isNetworkAvailable(mockContext));
    }

    @Test
    public void isNetworkAvailable_noTransport_returnsFalse() {
        Network mockNetwork = mock(Network.class);
        when(mockConnectivityManager.getActiveNetwork()).thenReturn(mockNetwork);
        when(mockConnectivityManager.getNetworkCapabilities(mockNetwork)).thenReturn(mockCapabilities);
        when(mockCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(false);
        when(mockCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)).thenReturn(false);

        assertFalse(NetworkUtils.isNetworkAvailable(mockContext));
    }
}
