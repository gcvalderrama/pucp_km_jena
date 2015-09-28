package com.pucp;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;
import com.hp.hpl.jena.util.FileManager;
import java.io.*;

public class Main {
    public static String RecetarioXS = "http://www.recetario.com/";
    static final String inputFileName  = "recetas.rdf";

    public static void main(String[] args) {
	// write your code here

        OntModel ont_model =  ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );

        FileManager.get().readModel( ont_model, inputFileName );

	    //  Model model = ModelFactory.createDefaultModel();

        // InputStream in = FileManager.get().open( inputFileName );

        //  if (in == null) {
        //      throw new IllegalArgumentException( "File: " + inputFileName + " not found");
        //  }

        // read the RDF/XML file
        // model.read(new InputStreamReader(in), "");


        // write it to standard out
        //ont_model.write(System.out);

        String prefix = "prefix recetas: <" + RecetarioXS + ">\n" +
                "prefix rdfs: <" + RDFS.getURI() + ">\n" +
                "prefix owl: <" + OWL.getURI() + ">\n";

        System.out.println(prefix);
        String q =  prefix +
                "select ?result where { ?result rdfs:subClassOf ?restriccion . \n" +
                " ?restriccion owl:onProperty recetas:tiene_ingrediente ; owl:someValuesFrom recetas:Tallarin }" ;

        Query query = QueryFactory.create(q);
        QueryExecution qexec = QueryExecutionFactory.create(query, ont_model);
        try {
            ResultSet results = qexec.execSelect();
            ResultSetFormatter.out( results, ont_model );
        }
        finally {
            qexec.close();
        }

       // System.out.println("hola mundo");
    }
}
