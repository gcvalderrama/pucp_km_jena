package com.pucp;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.reasoner.Derivation;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasonerFactory;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.vocabulary.*;
import com.hp.hpl.jena.util.FileManager;
import java.io.*;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasonerFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.PrintUtil;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;

import javax.swing.*;

public class Main {
    public static String RecetarioXS = "http://www.recetario.com/";
    static final String inputFileName  = "receta_base.owl";

    private static void PrintModel(Model model)
    {
        System.out.println("Printing Model========");
        StmtIterator iter = model.listStatements();
        while (iter.hasNext()) {
            Statement stmt      = iter.nextStatement();  // get next statement
            Resource  subject   = stmt.getSubject();     // get the subject
            Property  predicate = stmt.getPredicate();   // get the predicate
            RDFNode   object    = stmt.getObject();      // get the object

            System.out.print(subject.toString());
            System.out.print(" " + predicate.toString() + " ");
            if (object instanceof Resource) {
                System.out.print(object.toString());
            } else {
                // object is a literal
                System.out.print(" \"" + object.toString() + "\"");
            }
            System.out.println(" .");
        }
    }
    private  static void PrintResource(Resource resource)
    {
        StmtIterator iter = resource.listProperties();
        while (iter.hasNext()) {
            System.out.println("    " + iter.nextStatement()
                    .getObject()
                    .toString());
        }
    }

    private static void RecetarioSample()
    {
        Model instances = ModelFactory.createDefaultModel();

        instances.read("file:receta_base.owl");

        Model rule_model = ModelFactory.createDefaultModel();
        Resource configuration = rule_model.createResource();
        configuration.addProperty(ReasonerVocabulary.PROPruleSet, "rulesa.rdf");
        Reasoner reasoner = GenericRuleReasonerFactory.theInstance().create(configuration);
        InfModel inf = ModelFactory.createInfModel(reasoner, instances);


        Resource dietetica = inf.getResource("http://www.recetario.com/dietetica");

        StmtIterator iter = inf.listStatements(dietetica, null, (RDFNode) null );


        //StmtIterator iter = inf.listStatements();
        while (iter.hasNext()) {
            Statement stmt      = iter.nextStatement();
            Resource  subject   = stmt.getSubject();
            Property  predicate = stmt.getPredicate();
            RDFNode   object    = stmt.getObject();

            System.out.print(subject.toString());
            System.out.print(" " + predicate.toString() + " ");
            if (object instanceof Resource) {
                System.out.print(object.toString());
            } else {
                System.out.print(" \"" + object.toString() + "\"");
            }
            System.out.println(" .");
        }
    }
    private static  void RecetarioSample_ttl()
    {
        Model instances = ModelFactory.createDefaultModel();
        instances.read("file:recetas_turtle.ttl");
        //instances.read("file:receta_base.owl");

        Model rule_model = ModelFactory.createDefaultModel();
        Resource configuration = rule_model.createResource();
        configuration.addProperty(ReasonerVocabulary.PROPruleSet, "rules.rdf");
        Reasoner reasoner = GenericRuleReasonerFactory.theInstance().create(configuration);
        InfModel inf = ModelFactory.createInfModel(reasoner, instances);


        Resource dietetica = inf.getResource("http://www.recetario.com/#dietetica");

        StmtIterator iter = inf.listStatements(dietetica, null, (RDFNode) null );
        //StmtIterator iter = inf.listStatements();
        while (iter.hasNext()) {
            Statement stmt      = iter.nextStatement();
            Resource  subject   = stmt.getSubject();
            Property  predicate = stmt.getPredicate();
            RDFNode   object    = stmt.getObject();

            System.out.print(subject.toString());
            System.out.print(" " + predicate.toString() + " ");
            if (object instanceof Resource) {
                System.out.print(object.toString());
            } else {
                System.out.print(" \"" + object.toString() + "\"");
            }
            System.out.println(" .");
        }
    }

    private static void RecetasSparkQL ()
    {
        Model instances = ModelFactory.createDefaultModel();

        instances.read("file:receta_base.owl");

        String prefix = "prefix recetario: <" + RecetarioXS + ">\n" +
                "prefix rdfs: <" + RDFS.getURI() + ">\n" +
                "prefix owl: <" + OWL.getURI() + ">\n" +
                "prefix rdf: <" + RDF.getURI() + ">\n";

        System.out.println(prefix);

        String q =  prefix +
                "select ?result where { ?result rdfs:subClassOf ?restriccion . \n" +
                " ?restriccion owl:onProperty recetario:tiene_ingrediente ; owl:someValuesFrom recetario:Tallarin }" ;

        //altas calorias
        String q1 = prefix + "select DISTINCT ?receta where { " +
                " ?receta rdfs:subClassOf ?class . \n" +
                " ?class owl:onProperty recetario:tiene_ingrediente . \n" +
                " ?class owl:allValuesFrom ?allValues .\n" +
                " ?allValues owl:unionOf ?union . \n" +
                " ?union rdf:rest* [ rdf:first ?ingrediente ] . \n" +
                " ?ingrediente rdfs:subClassOf ?class2 .\n" +
                " ?class2 owl:onProperty recetario:nivelCalorias . \n" +
                " ?class2 owl:someValuesFrom ?nivel .\n" +
                " FILTER (strEnds(str(?nivel), \"Alto\")) }";

        //recetasa con carne o embutidos
        String where1 = "{ ?receta rdfs:subClassOf ?class . \n" +
                " ?class owl:onProperty recetario:tiene_ingrediente . \n" +
                " ?class owl:allValuesFrom ?allValues .\n" +
                " ?allValues owl:unionOf ?union . \n" +
                " ?union rdf:rest* [ rdf:first ?ingrediente ] . \n" +
                " ?ingrediente rdfs:subClassOf ?tipoIngrediente . \n" +
                " ?tipoIngrediente rdfs:subClassOf recetario:Ingrediente . \n" +
                " FILTER (strEnds(str(?tipoIngrediente), \"Carne\") || strEnds(str(?tipoIngrediente), \"Embutido\")) . \n }";

        String where2 = "{ ?receta rdfs:subClassOf ?class . \n" +
                " ?class owl:onProperty recetario:tiene_ingrediente . \n" +
                " ?class owl:allValuesFrom ?allValues .\n" +
                " ?allValues owl:unionOf ?union . \n" +
                " ?union rdf:rest* [ rdf:first ?subReceta ] . \n" +
                " ?subReceta rdfs:subClassOf ?esReceta . \n" +
                " ?esReceta rdfs:subClassOf recetario:Receta . \n" +
                " ?subReceta rdfs:subClassOf ?class2 . \n " +
                " ?class2 owl:onProperty recetario:tiene_ingrediente . \n " +
                " ?class2 owl:someValuesFrom ?ingrediente . \n " +
                " ?ingrediente rdfs:subClassOf ?tipoIngrediente . \n" +
                " ?tipoIngrediente rdfs:subClassOf recetario:Ingrediente . \n" +
                " FILTER (strEnds(str(?tipoIngrediente), \"Carne\") || strEnds(str(?tipoIngrediente), \"Embutido\")) . \n }";

        String q3 = prefix + "select ?receta where { " + where1 + " UNION " + where2 +
                " } GROUP BY ?receta \n " +
                " HAVING (count(?ingrediente) > 0) ";
        Query query = QueryFactory.create(q3);
        QueryExecution qexec = QueryExecutionFactory.create(query, instances);
        try {
            ResultSet results = qexec.execSelect();
            ResultSetFormatter.out( results, instances );
        }
        finally {
            qexec.close();
        }
    }
    public static void main(String[] args) {
        RecetasSparkQL();
    }
}
