package fr.gwan.parkinglots;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;

public class TestBase {

    protected static final String PARKING_LOT_NAME_TEST = "Test parking lot";

    protected static final String PARKING_SLOT_NAME_TEST = "Test parking slot %d";


    protected static final String PRICING_POLICY_SEDAN_TEST = "10 * h";
    protected static final String PRICING_POLICY_20KW_TEST = "10.5 * h + 20";
	protected static final String PRICING_POLICY_50KW_TEST = "12 * h * h + 2";


    protected static final HttpHeaders subscriberRequestHeader = new HttpHeaders();

    protected static final HttpHeaders operatorRequestHeader = new HttpHeaders();

    protected static final String URL_BASE = "https://localhost:PORT/api/";

    protected static final String LOGIN_USER_TEST = "integration-ecosystem+dev1TestOnly-sub@actility.com";

    protected static final String PASSWORD_USER_TEST = "Azerty1234";

    protected static final String SUBSCRIBER_REF_TEST = "1306";
    
    protected static final String CUSTOMER_ID_TEST = "100001306";
    
    protected static final String LOGIN_OPE_TEST = "integration-ecosystem+dev1-system-dx-apionly@actility.com";

    protected static final String PASSWORD_OPE_TEST = "Q8ZdL9ROXY";

    protected static final String GENERIC_BODY_CREATION = "{\"id\":\"IDDATAFLOW\",\"name\":\"NAMEDATAFLOW\",\"bidirectional\":true,\"binder\":{\"classRef\":\"LRC_HTTP\",\"properties\":{\"deviceEUIList\":\"70B3D5326000027F\"}},\"driver\":{\"classRef\":\"Raw\"},\"connectors\":[{\"classRef\":\"AzureIoTHub\",\"properties\":{\"sendMetadata\":\"false\",\"downlinkPort\":\"2\",\"hostName\":\"actilitydemo.azure-devices.net\",\"sharedAccessKeyName\":\"actilityowner\",\"sharedAccessKey\":\"tEBsWSgZ2/nd1zYhWsJvTq9LDjlMIHvs/8yF3hV93vA=\",\"protocol\":\"MQTT\"}}]}";

    protected static final String ID_DATAFLOW_STRING = "IDDATAFLOW";

    protected static final String NAME_DATAFLOW_STRING = "NAMEDATAFLOW";

    protected static final String DEVICE_STRING = "70B3D5326000027F";

    protected static final String REF_STRING = "REF";

    protected static Map<String, String> testDataStorage = new HashMap<>();

}