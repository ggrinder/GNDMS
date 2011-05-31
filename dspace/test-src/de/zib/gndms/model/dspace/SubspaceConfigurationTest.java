package de.zib.gndms.model.dspace;

/*
 * Copyright 2008-2011 Zuse Institute Berlin (ZIB)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import de.zib.gndms.logic.model.config.SetupAction.SetupMode;
import de.zib.gndms.model.dspace.SubspaceConfiguration;
import de.zib.gndms.model.dspace.WrongConfigurationException;
import de.zib.gndms.stuff.confuror.ConfigEditor;
import de.zib.gndms.stuff.confuror.ConfigEditor.UpdateRejectedException;
import de.zib.gndms.stuff.confuror.ConfigHolder;

/**
 * Tests the SubspaceConfiguration.
 * @author Ulrike Golas
 *
 */
public class SubspaceConfigurationTest{
	
	/**
	 * Tests the method checkSubspaceConfiguration(ConfigHolder) with a valid configuration
	 * and the access to the subspace configuration fields.
	 * @throws IOException 
	 * @throws UpdateRejectedException 
	 * @throws WrongConfigurationException 
	 */
	@Test
    public final void testCheckAndGet1() throws IOException, UpdateRejectedException, WrongConfigurationException {
		ConfigHolder testConfig = new ConfigHolder();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonFactory factory = objectMapper.getJsonFactory();
		ConfigEditor.Visitor visitor = new ConfigEditor.DefaultVisitor();
		ConfigEditor editor = testConfig.newEditor(visitor);
		testConfig.setObjectMapper(objectMapper);
		
		// Construct a valid vonfiguration
		String path = "testpath";
		String gsiftp = "gsiftp";
		boolean visible = true;
		final long value = 6000;
		SetupMode mode = SetupMode.valueOf("UPDATE");
		JsonNode pn = ConfigHolder.parseSingle(factory, "{ '" + SubspaceConfiguration.PATH + "': '" + path + "' }");
		JsonNode gn = ConfigHolder.parseSingle(factory, "{ '" + SubspaceConfiguration.GSIFTPPATH + "': '" + gsiftp + "' }");
		JsonNode vn = ConfigHolder.parseSingle(factory, "{ '" + SubspaceConfiguration.VISIBLE +"': " + visible + " }");
		JsonNode sn = ConfigHolder.parseSingle(factory, "{ '" + SubspaceConfiguration.SIZE + "': " + value + " }");
		JsonNode mn = ConfigHolder.parseSingle(factory, "{ '" + SubspaceConfiguration.MODE +"': '" + mode + "' }");
		testConfig.update(editor, pn);
		testConfig.update(editor, gn);
		testConfig.update(editor, vn);
		testConfig.update(editor, sn);
		testConfig.update(editor, mn);

       	AssertJUnit.assertEquals(true, SubspaceConfiguration.checkSubspaceConfiguration(testConfig));
       	
       	String testPath = SubspaceConfiguration.getPath(testConfig);
       	String testGsiftp = SubspaceConfiguration.getGsiFtpPath(testConfig);
       	boolean testVisible = SubspaceConfiguration.getVisibility(testConfig);
       	long testValue = SubspaceConfiguration.getSize(testConfig);
       	SetupMode testMode = SubspaceConfiguration.getMode(testConfig);

       	AssertJUnit.assertEquals(path, testPath);
       	AssertJUnit.assertEquals(gsiftp, testGsiftp);
       	AssertJUnit.assertEquals(visible, testVisible);
       	AssertJUnit.assertEquals(value, testValue);
       	AssertJUnit.assertEquals(mode, testMode);

	}

	/**
	 * Tests the method checkSubspaceConfiguration(ConfigHolder) with invalid configurations.
	 * @throws IOException 
	 * @throws UpdateRejectedException 
	 */
	@Test
    public final void testCheckAndGet2() throws IOException, UpdateRejectedException {
		ConfigHolder testConfig = new ConfigHolder();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonFactory factory = objectMapper.getJsonFactory();
		ConfigEditor.Visitor visitor = new ConfigEditor.DefaultVisitor();
		ConfigEditor editor = testConfig.newEditor(visitor);
		testConfig.setObjectMapper(objectMapper);
		
		final long testValue = 6000;
		
		// empty node
       	AssertJUnit.assertEquals(false, SubspaceConfiguration.checkSubspaceConfiguration(testConfig));
       	try {
			SubspaceConfiguration.getPath(testConfig);
			AssertJUnit.fail();
		} catch (WrongConfigurationException e) {
		}
       	
       	// only path set - gsi ftp path is missing
		JsonNode node = ConfigHolder.parseSingle(factory, "{ '" + SubspaceConfiguration.PATH + "': 'testpath' }");
		testConfig.update(editor, node);

       	AssertJUnit.assertEquals(false, SubspaceConfiguration.checkSubspaceConfiguration(testConfig));
       	try {
			SubspaceConfiguration.getGsiFtpPath(testConfig);
			AssertJUnit.fail();
		} catch (WrongConfigurationException e) {
		}

       	// only path and gsi ftp path set - visibility is missing
       	node = ConfigHolder.parseSingle(factory, "{ '" + SubspaceConfiguration.GSIFTPPATH + "': 'gsiftp' }");
		testConfig.update(editor, node);

       	AssertJUnit.assertEquals(false, SubspaceConfiguration.checkSubspaceConfiguration(testConfig));
       	try {
			SubspaceConfiguration.getVisibility(testConfig);
			AssertJUnit.fail();
		} catch (WrongConfigurationException e) {
		}

       	// only path, gsi ftp path and visibility set - size is missing
		node = ConfigHolder.parseSingle(factory, "{ '" + SubspaceConfiguration.VISIBLE +"': true }");
		testConfig.update(editor, node);
       	try {
			SubspaceConfiguration.getSize(testConfig);
			AssertJUnit.fail();
		} catch (WrongConfigurationException e) {
		}

       	AssertJUnit.assertEquals(false, SubspaceConfiguration.checkSubspaceConfiguration(testConfig));

       	// only path, gsi ftp path, visibility, and size set - mode is missing
		node = ConfigHolder.parseSingle(factory, "{ '" + SubspaceConfiguration.SIZE + "': " + testValue + " }");
		testConfig.update(editor, node);
       	try {
			SubspaceConfiguration.getMode(testConfig);
			AssertJUnit.fail();
		} catch (WrongConfigurationException e) {
		}

       	AssertJUnit.assertEquals(false, SubspaceConfiguration.checkSubspaceConfiguration(testConfig));

       	// wrong type of path value - number instead of string
		node = ConfigHolder.parseSingle(factory, "{ '" + SubspaceConfiguration.PATH + "': " + testValue + " }");
		testConfig.update(editor, node);

		AssertJUnit.assertEquals(false, SubspaceConfiguration.checkSubspaceConfiguration(testConfig));
       	try {
			SubspaceConfiguration.getPath(testConfig);
			AssertJUnit.fail();
		} catch (WrongConfigurationException e) {
		}

       	// wrong type of gsi ftp path value - number instead of string
		node = ConfigHolder.parseSingle(factory, "{ '" + SubspaceConfiguration.GSIFTPPATH + "': " + testValue + " }");
		testConfig.update(editor, node);

		AssertJUnit.assertEquals(false, SubspaceConfiguration.checkSubspaceConfiguration(testConfig));
       	try {
			SubspaceConfiguration.getGsiFtpPath(testConfig);
			AssertJUnit.fail();
		} catch (WrongConfigurationException e) {
		}


       	// wrong type of visibility value - number instead of boolean
		node = ConfigHolder.parseSingle(factory, "{ '" + SubspaceConfiguration.VISIBLE + "': " + testValue + " }");
		testConfig.update(editor, node);

		AssertJUnit.assertEquals(false, SubspaceConfiguration.checkSubspaceConfiguration(testConfig));
       	try {
			SubspaceConfiguration.getVisibility(testConfig);
			AssertJUnit.fail();
		} catch (WrongConfigurationException e) {
		}


       	// wrong type of size value - string instead of number
		node = ConfigHolder.parseSingle(factory, "{ '" + SubspaceConfiguration.SIZE + "': 'testpath' }");
		testConfig.update(editor, node);

		AssertJUnit.assertEquals(false, SubspaceConfiguration.checkSubspaceConfiguration(testConfig));
       	try {
			SubspaceConfiguration.getSize(testConfig);
			AssertJUnit.fail();
		} catch (WrongConfigurationException e) {
		}

       	// wrong type of mode value - number instead of string
		node = ConfigHolder.parseSingle(factory, "{ '" + SubspaceConfiguration.MODE + "': " + testValue + " }");
		testConfig.update(editor, node);

		AssertJUnit.assertEquals(false, SubspaceConfiguration.checkSubspaceConfiguration(testConfig));
       	try {
			SubspaceConfiguration.getMode(testConfig);
			AssertJUnit.fail();
		} catch (WrongConfigurationException e) {
		}

	}
}