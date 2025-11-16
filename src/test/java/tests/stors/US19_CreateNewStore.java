package tests.stors;

import base_urls.apiBazaar;
import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.Test;

import static utilities.ObjectMapperUtils.getJsonNode;

public class US19_CreateNewStore extends apiBazaar {

//[US19_TC001] [Positive] Successful Store Creation with All Required Fields
    @Test
    public void testStoreCreationwithAllRequiredFields() {
        //Prepare the expected data
        JsonNode payload = getJsonNode("AddStore");

    }
}

