package com.vincent.vpedometer.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.vincent.vpedometer.R;
import com.vincent.vpedometer.utils.PermissionUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Administrator on 2018/3/27 0:33
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnInfoWindowClickListener {

    GoogleMap map = null;//the map API
    private LocationRequest mLocationRequest;// the location of user
    private GoogleApiClient mGoogleApiClient;//call map
    private Location mLastLocation;//recording the last postion of user

    private final int[] MAP_TYPES = {GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE};//the type of the google map rendering

    private GMapV2Direction mMd; // the paser of XML
    private Polyline mPolylin; // the red line to display direction

    private Marker mCurrentMarker;//where user want to go
    private TextView mTextView;//the information of time and dinstance and steps

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_map);
        mTextView = (TextView) findViewById(R.id.information);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //create google api
        buildGoogleApiClient();
        //connect to google service api
        mGoogleApiClient.connect();
        if (map == null) {
            //inite rendering the fragment of map
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }


    }


    /**
     * when google map ready this method  will be invoked
     *
     * @param googleMap the google map API
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        //set the listener to listen event when user click the map
        map.setOnMarkerClickListener(this);
        map.setOnMapClickListener(this);
        map.setOnMapLongClickListener(this);
        setUpMap();
    }


    /**
     * location the user current position.
     * need the permission
     */
    private void setUpMap() {
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        PermissionUtils.performCodeWithPermission(this, "google map access your location", new PermissionUtils.PermissionCallback() {

            @Override
            public void hasPermission() {
                Toast.makeText(MapActivity.this, "you have grant the permission", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void noPermission() {
                Toast.makeText(MapActivity.this, "you have reject the permission", Toast.LENGTH_SHORT).show();
            }
        }, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);

    }


    /**
     * create the google api and add connection listener
     */
    protected synchronized void buildGoogleApiClient() {
        //Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    /**
     * when connect google api server correct this method will be invoked
     *
     * @param bundle the message
     */
    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(4000);//how many mili second to locate user
        mLocationRequest.setFastestInterval(1000);//the locate service can not be call less than 1 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);//the power saver model
        PermissionUtils.performCodeWithPermission(this, "google map access your location", new PermissionUtils.PermissionCallback() {
            @Override
            public void hasPermission() {
                Toast.makeText(MapActivity.this, "you have grant the permission", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void noPermission() {
                Toast.makeText(MapActivity.this, "you have reject the permission", Toast.LENGTH_SHORT).show();
            }
        }, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //request the loaction(continuesly)
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }


    @Override
    public void onConnectionSuspended(int i) {
        //Connection Suspended
        Toast.makeText(this, "onConnectionSuspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Connection Failed
        Toast.makeText(this, "onConnectionFailed", Toast.LENGTH_SHORT).show();
    }

    /**
     * when the user location has changed the method will be called
     *
     * @param location the last position of user
     */
    @Override
    public void onLocationChanged(Location location) {
        //set to member variable
        mLastLocation = location;
        double dLatitude = mLastLocation.getLatitude();
        double dLongitude = mLastLocation.getLongitude();
        //update the red line and information
        LatLng myLatLng = new LatLng(dLatitude, dLongitude);
        if (mCurrentMarker == null || mMd == null)
            return;

        mMd.getDocument(myLatLng, mCurrentMarker.getPosition(),
                GMapV2Direction.MODE_WALKING);
        int distance = mMd.getDistanceValue(doc);
        int v = (int) (distance / 0.7);
        mTextView.setText("time :" + mMd.getDurationText(doc) + " distance:" + mMd.getDistanceText(doc) + " steps:" + v);

    }

    //the marker list
    List<Marker> marks = new ArrayList<Marker>();

    /**
     * create the marker and add it to the map
     *
     * @param latLng
     */
    @Override
    public void onMapClick(LatLng latLng) {
        // the marker information
        MarkerOptions options = new MarkerOptions().position(latLng);
        options.title(getAddressFromLatLng(latLng));
        options.icon(BitmapDescriptorFactory.defaultMarker());

        //create the marker
        Marker marker = map.addMarker(options);
        if (mCurrentMarker != null)
            mCurrentMarker.remove();
        mCurrentMarker = marker;

    }

    /*
     *get the latLng name may the building name or the street name
     */
    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this);
        String address = "";
        try {
            address = geocoder
                    .getFromLocation(latLng.latitude, latLng.longitude, 1)
                    .get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }


    /**
     * show the marker on the map
     *
     * @param marker your marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }

    /**
     * draw the red line
     */
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                ArrayList<LatLng> directionPoint = mMd.getDirection(doc);
                PolylineOptions rectLine = new PolylineOptions().width(3).color(
                        Color.RED);

                for (int i = 0; i < directionPoint.size(); i++) {
                    rectLine.add(directionPoint.get(i));
                }
                if (mPolylin != null)
                    mPolylin.remove();
                mPolylin = map.addPolyline(rectLine);
            }
        }
    };


    /**
     * the call back function when permission has result
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != PermissionUtils.permissionRequestCode) {
            return;
        }
        PermissionUtils.verifyPermissions(grantResults);
    }


    /**
     * @param marker
     */
    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    /**
     * show the marker information and delete the red line
     *
     * @param latLng the position
     */
    @Override
    public void onMapLongClick(LatLng latLng) {
        if (mCurrentMarker != null)
            mCurrentMarker.remove();

        if (mPolylin != null) {
            mPolylin.remove();
        }


    }

    Document doc = null;

    /**
     * the class for indicate the location bewteen the source point and end point
     */
    class GMapV2Direction {
        public final static String MODE_DRIVING = "driving";
        public final static String MODE_WALKING = "walking";

        public GMapV2Direction() {
        }

        /**
         * connect to google Directions API
         *
         * @param start your position
         * @param end   where you want to go
         * @param mode  walking or driving
         * @return the xml document
         */
        public Document getDocument(LatLng start, LatLng end, String mode) {
            final String url = "https://maps.googleapis.com/maps/api/directions/xml?"
                    + "key=AIzaSyA-jnzRpsLS-CirIIkVdIq4G4sJNQhS3qQ"
                    + "&origin=" + start.latitude + "," + start.longitude
                    + "&destination=" + end.latitude + "," + end.longitude
                    + "&sensor=false&units=metric&mode=" + mode;

            Log.d("url", url);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpContext localContext = new BasicHttpContext();
                        HttpPost httpPost = new HttpPost(url);
                        HttpResponse response = httpClient.execute(httpPost, localContext);
                        InputStream in = response.getEntity().getContent();
                        DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                                .newDocumentBuilder();
                        doc = builder.parse(in);
                        Message message = myHandler.obtainMessage(1);
                        myHandler.sendMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            return doc;
        }


        /**
         * get the duration from source to end
         *
         * @param doc xml
         * @return the text
         */
        public String getDurationText(Document doc) {
            try {

                NodeList durationNodes = doc.getElementsByTagName("duration");
                Node durationNode = durationNodes.item(durationNodes.getLength() - 1);
                NodeList childDurationNodes = durationNode.getChildNodes();
                Node textDurationNode = childDurationNodes.item(getNodeIndex(childDurationNodes, "text"));
                Log.i("DurationText", textDurationNode.getTextContent());
                return textDurationNode.getTextContent();
            } catch (Exception e) {
                return "0";
            }
        }

        public int getDurationValue(Document doc) {
            try {
                NodeList durationNodes = doc.getElementsByTagName("duration");
                Node node1 = durationNodes.item(0);
                NodeList childDurationNodes = node1.getChildNodes();
                Node value = childDurationNodes.item(getNodeIndex(childDurationNodes, "value"));
                Log.i("DurationValue", value.getTextContent());
                return Integer.parseInt(value.getTextContent());
            } catch (Exception e) {
                return -1;
            }
        }

        public String getDistanceText(Document doc) {
            try {
                NodeList nl1;
                nl1 = doc.getElementsByTagName("distance");
                Node distanceNode = nl1.item(nl1.getLength() - 1);
                NodeList distanceNodes = null;
                distanceNodes = distanceNode.getChildNodes();
                Node textDistanceNode = distanceNodes.item(getNodeIndex(distanceNodes, "value"));
                Log.d("DistanceText", textDistanceNode.getTextContent());
                return textDistanceNode.getTextContent();
            } catch (Exception e) {
                return "-1";
            }

        }

        public int getDistanceValue(Document doc) {
            try {
                NodeList nl1 = doc.getElementsByTagName("distance");
                Node distanceNode = null;
                distanceNode = nl1.item(nl1.getLength() - 1);
                NodeList distanceNodes = distanceNode.getChildNodes();
                Node value = distanceNodes.item(getNodeIndex(distanceNodes, "value"));
                Log.i("DistanceValue", value.getTextContent());
                return Integer.parseInt(value.getTextContent());
            } catch (Exception e) {
                return -1;
            }
        }

        public String getStartAddress(Document doc) {
            try {
                NodeList nl1 = doc.getElementsByTagName("start_address");
                Node node1 = nl1.item(0);
                Log.i("StartAddress", node1.getTextContent());
                return node1.getTextContent();
            } catch (Exception e) {
                return "-1";
            }

        }

        public String getEndAddress(Document doc) {
            try {
                NodeList nl1 = doc.getElementsByTagName("end_address");
                Node node1 = nl1.item(0);
                Log.i("StartAddress", node1.getTextContent());
                return node1.getTextContent();
            } catch (Exception e) {
                return "-1";
            }
        }

        public String getCopyRights(Document doc) {
            try {
                NodeList nl1 = doc.getElementsByTagName("copyrights");
                Node node1 = nl1.item(0);
                Log.i("CopyRights", node1.getTextContent());
                return node1.getTextContent();
            } catch (Exception e) {
                return "-1";
            }

        }


        /**
         * parse the xml file from google map server.
         *
         * @param doc the xml file
         * @return the location point
         */

        public ArrayList<LatLng> getDirection(Document doc) {
            NodeList nl1, nl2, nl3;
            ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();
            nl1 = doc.getElementsByTagName("step");
            if (nl1.getLength() > 0) {
                for (int i = 0; i < nl1.getLength(); i++) {
                    Node node1 = nl1.item(i);
                    nl2 = node1.getChildNodes();

                    Node locationNode = nl2
                            .item(getNodeIndex(nl2, "start_location"));
                    nl3 = locationNode.getChildNodes();
                    Node latNode = nl3.item(getNodeIndex(nl3, "lat"));
                    double lat = Double.parseDouble(latNode.getTextContent());
                    Node lngNode = nl3.item(getNodeIndex(nl3, "lng"));
                    double lng = Double.parseDouble(lngNode.getTextContent());
                    listGeopoints.add(new LatLng(lat, lng));

                    locationNode = nl2.item(getNodeIndex(nl2, "polyline"));
                    nl3 = locationNode.getChildNodes();
                    latNode = nl3.item(getNodeIndex(nl3, "points"));
                    ArrayList<LatLng> arr = decodePoly(latNode.getTextContent());
                    for (int j = 0; j < arr.size(); j++) {
                        listGeopoints.add(new LatLng(arr.get(j).latitude, arr
                                .get(j).longitude));
                    }

                    locationNode = nl2.item(getNodeIndex(nl2, "end_location"));
                    nl3 = locationNode.getChildNodes();
                    latNode = nl3.item(getNodeIndex(nl3, "lat"));
                    lat = Double.parseDouble(latNode.getTextContent());
                    lngNode = nl3.item(getNodeIndex(nl3, "lng"));
                    lng = Double.parseDouble(lngNode.getTextContent());
                    listGeopoints.add(new LatLng(lat, lng));
                }
            }
            return listGeopoints;
        }


        private int getNodeIndex(NodeList nl, String nodename) {
            for (int i = 0; i < nl.getLength(); i++) {
                if (nl.item(i).getNodeName().equals(nodename))
                    return i;
            }
            return -1;
        }

        private ArrayList<LatLng> decodePoly(String encoded) {
            ArrayList<LatLng> poly = new ArrayList<LatLng>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;
            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;
                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
                poly.add(position);
            }
            return poly;
        }
    }

    /**
     * to get the line of the routing
     *
     * @param view
     */
    public void route(View view) {
        mMd = new GMapV2Direction();
        LatLng myLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        if (mCurrentMarker == null) {
            Toast.makeText(this, "you have no destine location", Toast.LENGTH_SHORT).show();
            return;
        }
        mMd.getDocument(myLatLng, mCurrentMarker.getPosition(),
                GMapV2Direction.MODE_WALKING);
        int distance = mMd.getDistanceValue(doc);
        int v = (int) (distance / 0.7);
        mTextView.setText("time :" + mMd.getDurationText(doc) + " distance:" + mMd.getDistanceText(doc) + " steps:" + v);
    }
}
