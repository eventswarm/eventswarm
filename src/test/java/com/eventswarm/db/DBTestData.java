/**
 * Copyright 2007-2014 Ensift Pty Ltd as trustee for the Avaz Trust and other contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
/*
 * DBTestData.java
 *
 * Created on May 23, 2007, 4:51 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.eventswarm.db;

/** Class to create a known dataset for LetsWent a dump it out to a file for use
 * by dbunit
 *
 * This script will clear your "letswent" schema from the database and repopulate
 * with the test data, then dump it out to an XML file for use in testing.  You 
 * can subclass this and override the "populate" method to create your own test
 * data.
 *
 * The default database URL is a local instance of MySQL and "letswent" schema.
 * Override or reset the static class members if you want to use a different URL or 
 * database driver.
 *
 * @author andyb
 */
public class DBTestData {
    
    // Database connection parameters
    protected static String driver = "com.mysql.jdbc.Driver";
    protected static String dbURL = "jdbc:mysql://localhost:3306/letswent";
    protected static String user = "letswent";
    protected static String password = "letswent";
    
    /** no constructor required
     */
    private DBTestData() {

    }
    
    
    /** Connect to the database and clear it */
    static protected void connect() {
        
    }
    
    /** Connect to the database and clear it */
    static protected void clear() {
        
    }
    
    static protected void disconnect() {
        
    }
    
    /** Method to call and populate the database.  Use our codebase to create 
     * activities, users etc.
     */
    static protected void populate() {
    
    }
    
    /** Save the data */
    static protected void save(String filename) {
        
    }
    
    /** Main method to run the database population code */
    public static void main(String[] argv) {
        
        // Connect to the database 
        connect();
        
        // clear it
        clear();
        
        // Populate it again
        populate();
        
        // Save it
        String filename;
        if (argv.length > 0) { filename = argv[0];}
        else { filename = "DBTestData.xml";}
        save(filename);
        
        // Disconnect
        disconnect();
        
        // Done
        System.exit(0);
    }
    
}
