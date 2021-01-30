package com.swissre;

import java.util.logging.Logger;

public class LixDemo_document_query_metadata extends LixDemo_bundles_query{

    private static final Logger logger = Logger.getLogger(LixDemo_document_query_metadata.class.getName());

    public static void main(String[] args) {
        String token = getToken();

        System.out.println("token: "+token);
        System.out.println();
        System.out.println();

        String documentId = "9b40e7f2-15b3-4ea6-ac04-49df74005b5d";
        String url= "https://lix.cf-dev.swissre.cn/public/api/v1/cdms/collections/2/documents/"+documentId;
        LixDemo_document_query_metadata.doGetTestOne(token, url);
    }

}