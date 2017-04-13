package com.android.viewbinder.scanner;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by parkjaesung on 2017. 4. 13..
 */
public class ClassParser {

    /**
     * The constant instance.
     */
    public static ClassParser instance;

    private ClassParser(){

    }

    /**
     * Get instance class parser.
     *
     * @return the class parser
     */
    public static ClassParser getInstance(){

        if(instance == null){
            return instance = new ClassParser();
        }
        return instance;
    }

    /**
     * Gets class name.
     *
     * @param classFile the class file
     * @return the class name
     * @throws FileNotFoundException the file not found exception
     * @throws ParseException        the parse exception
     */
    public String getClassName(File classFile) throws FileNotFoundException, ParseException {
        CompilationUnit compilationunit = getCompilationUnit(classFile);

        return compilationunit.getTypes().get(0).getName();
    }

    /**
     * Get class name string.
     *
     * @param compilationunit the compilationunit
     * @return the string
     */
    public String getClassName(CompilationUnit compilationunit){
        return compilationunit.getTypes().get(0).getName();
    }


    /**
     * Gets parent class name.
     *
     * @param classFile the class file
     * @return the parent class name
     * @throws FileNotFoundException the file not found exception
     * @throws ParseException        the parse exception
     */
    public String getParentClassName(File classFile) throws FileNotFoundException, ParseException {
        ArrayList<String> classSources = getClassSourceList(classFile);

        return classSources.get(classSources.indexOf("extends")+1);
    }

    /**
     * Gets parent class name.
     *
     * @param compilationunit the compilationunit
     * @return the parent class name
     * @throws FileNotFoundException the file not found exception
     * @throws ParseException        the parse exception
     */
    public String getParentClassName(CompilationUnit compilationunit) throws FileNotFoundException, ParseException {
        ArrayList<String> classSources = getClassSourceList(compilationunit);

        return classSources.get(classSources.indexOf("extends")+1);
    }

    /**
     * Gets interface name.
     *
     * @param classFile the class file
     * @return the interface name
     * @throws FileNotFoundException the file not found exception
     * @throws ParseException        the parse exception
     */
    public String getInterfaceName(File classFile) throws FileNotFoundException, ParseException {
        ArrayList<String> classSources = getClassSourceList(classFile);

        return classSources.get(classSources.indexOf("implements")+1);
    }

    /**
     * Gets interface name.
     *
     * @param compilationunit the compilationunit
     * @return the interface name
     * @throws FileNotFoundException the file not found exception
     * @throws ParseException        the parse exception
     */
    public String getInterfaceName(CompilationUnit compilationunit) throws FileNotFoundException, ParseException {
        ArrayList<String> classSources = getClassSourceList(compilationunit);

        return classSources.get(classSources.indexOf("implements")+1);
    }

    private CompilationUnit getCompilationUnit(File file) throws ParseException, FileNotFoundException {
        FileInputStream in = new FileInputStream(file);
        CompilationUnit compilationunit = JavaParser.parse(in);

        return compilationunit;
    }

    @NotNull
    private ArrayList<String> getClassSourceList(File classFile) throws ParseException, FileNotFoundException {
        CompilationUnit compilationunit = getCompilationUnit(classFile);
        String classSource = compilationunit.getTypes().get(0).toString();
        return new ArrayList<String>(Arrays.asList(classSource.split(" ")));
    }

    @NotNull
    private ArrayList<String> getClassSourceList(CompilationUnit compilationunit) throws ParseException, FileNotFoundException {
        String classSource = compilationunit.getTypes().get(0).toString();
        return new ArrayList<String>(Arrays.asList(classSource.split(" ")));
    }
}