/*
 *                    BioJava development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual
 * authors.  These should be listed in @author doc comments.
 *
 * For more information on the BioJava project and its aims,
 * or to join the biojava-l mailing list, visit the home page
 * at:
 *
 *      http://www.biojava.org/
 *
 * Created on Jan 25, 2010
 *
 */
package org.biojava.bio.structure;

import java.io.InputStream;

import junit.framework.TestCase;

import org.biojava.bio.structure.align.StructureAlignment;
import org.biojava.bio.structure.align.StructureAlignmentFactory;
import org.biojava.bio.structure.align.ce.CeMain;
import org.biojava.bio.structure.align.model.AFPChain;
import org.biojava.bio.structure.align.util.AtomCache;
import org.biojava.bio.structure.align.xml.AFPChainXMLConverter;
import org.biojava.bio.structure.align.xml.AFPChainXMLParser;
import org.biojava.bio.structure.io.mmcif.ChemCompGroupFactory;
import org.biojava.bio.structure.io.mmcif.ChemCompProvider;
import org.biojava.bio.structure.io.mmcif.DownloadChemCompProvider;
import org.biojava.bio.structure.util.StringManipulationTestsHelper;
import org.biojava3.core.util.StringManipulationHelper;

/** This test makes sure that the new representation of selenocysteins as SEC amino acids does not
 * affect the structure alignment results.
 * 
 * @author andreas
 *
 */
public class TestSECalignment extends  TestCase {

	public void testOldSecOutput() throws Exception {

		ChemCompProvider ccprovorig = ChemCompGroupFactory.getChemCompProvider();
		System.out.println(ccprovorig);
		
		
		if ( ! (ccprovorig instanceof DownloadChemCompProvider)) {
			ChemCompProvider ccprov = new DownloadChemCompProvider();
			//System.out.println("changing ChemComp Provider");
			ChemCompGroupFactory.setChemCompProvider(ccprov);
		}
		
		String fileName = "/ce_1fdo.A_2iv2.X.out";
		InputStream inStream = this.getClass().getResourceAsStream(fileName);
		assertNotNull("Could not find file " + fileName +" in resource path. Config error?", inStream);
		String xml = StringManipulationHelper.convertStreamToString(inStream);

		AtomCache cache = new AtomCache();
		String name1="1FDO.A";
		String name2="2IV2.X";
		Atom[] ca1 = cache.getAtoms(name1);
		Atom[] ca2 = cache.getAtoms(name2);

		assertEquals(715, ca1.length);
		assertEquals(697, ca2.length);
		
		System.out.println("recreating afpChain from XML");
		AFPChain afpChainOrig = AFPChainXMLParser.fromXML(xml, ca1, ca2);
		//alnseq1,alnseq2
		
		assertNotNull("Could not get AfpChain object from flat file!", afpChainOrig);
		
		assertEquals("Could not find alignment string for prot 1","MKKVVTVCPYCASGCKINLVVDNGKIVRAEAAQGKTNQGTLCLKGYYGWDFINDTQILTPRLKTPMIRRQRGGKLEPVSWDEALNYVAERLSAIKEKYGPDAIQTTGSSRGTGNETNYVMQKFARAVIGTNNVDCCARVUHGPSVA-----GLHQSVGNGAMSNAINEIDNTDLVFVFGYNPADSHPIVANHVINAKRNGAKIIVCDPRKIETARIADMHIALKNGSNIALLNAMGHVIIEENLYDKAFVASRTEGFEEYRKIVEGYTPESVEDITGVSASEIRQAARMYAQAKSAAILWGMGVTQFYQGVETVRSLTSLAMLTGNLGKPHAGVNPVRGQNNVQGACDMGALPDTYPGYQYVKDPANREKFAKAWGVESLPAHTGYRISELPHRAAHGEVRAAYIMGEDPLQTDAELSAVRKAFEDLELVIVQDIFMTKTASAADVILPSTSWGEHEGVFTAADRGFQRFFKAVEPKWDLKTDWQIISEIATRMGYPMHYNNTQEIWDELRHLCPDFYGATYEKMGELGFIQWPCRDTSDADQGTSYLFKEKFDTPNGLAQFFTCDWVAPIDKLTDEYPMVLSTVREVGHYSCRSMTGNCAALAALADEPGYAQINTEDAKRLGIEDEALVWVHSRKGKIITRAQVSDRPNKGAIYMTYQWWIGACNELVTENLSPITKTPEYKYCAVRVEPIADQRAAEQYVIDEYNKLKTRLREAALA                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     ",new String(afpChainOrig.getAlnseq1()));
		assertEquals("Could not find alignment string for prot 2","MKKVVTVCPYCASGCKINLVVDNGKIVRAEAAQGKTNQGTLCLKGYYGWDFINDTQILTPRLKTPMIRRQRGGKLEPVSWDEALNYVAERLSAIKEKYGPDAIQTTGSSRGTGNETNYVMQKFARAVIGTNNVDCCAR-----VUHGPSVAGLHQSVGNGAMSNAINEIDNTDLVFVFGYNPADSHPIVANHVINAKRNGAKIIVCDPRKIETARIADMHIALKNGSNIALLNAMGHVIIEENLYDKAFVASRTEGFEEYRKIVEGYTPESVEDITGVSASEIRQAARMYAQAKSAAILWGMGVTQFYQGVETVRSLTSLAMLTGNLGKPHAGVNPVRGQNNVQGACDMGALPDTYPGYQYVKDPANREKFAKAWGVESLPAHTGYRISELPHRAAHGEVRAAYIMGEDPLQTDAELSAVRKAFEDLELVIVQDIFMTKTASAADVILPSTSWGEHEGVFTAADRGFQRFFKAVEPKWDLKTDWQIISEIATRMGYPMHYNNTQEIWDELRHLCPDFYGATYEKMGELGFIQWPCRDTSDADQGTSYLFKEKFDTPNGLAQFFTCDWVAPIDKLTDEYPMVLSTVREVGHYSCRSMTGNCAALAALADEPGYAQINTEDAKRLGIEDEALVWVHSRKGKIITRAQVSDRPNKGAIYMTYQWW------------------PEYKYCAVRVEPIADQRAAEQYVIDEYNKLKTRLREAALA                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     ", new String(afpChainOrig.getAlnseq2()));
		
		//System.out.println("after parsing: " + afpChainOrig.getIdentity());
		//System.out.println(afpChainOrig.getSimilarity());
		// calc time is hardware dependent.... overwrite...
		afpChainOrig.setCalculationTime(-1);

		assertEquals("alnLength is wrong! (" + afpChainOrig.getAfpChainLen()+")" ,
				720,afpChainOrig.getAlnLength());
		assertEquals("gapLength is wrong! ("+ afpChainOrig.getGapLen() + ")",    
				28, afpChainOrig.getGapLen());
		
		//identity should be 0.9957
		assertTrue("alinment ID is < 0.99 ! (" + afpChainOrig.getIdentity()+")" , afpChainOrig.getIdentity() > 0.99);
		assertTrue("alignment ID is > 1 ! (" + afpChainOrig.getIdentity()+")" ,afpChainOrig.getIdentity() < 1.0);
		
		//String ce1 = afpChainOrig.toFatcat(ca1, ca2);
	
		System.out.println("getting XML from re-created afpChain");
		String xmlComp =  AFPChainXMLConverter.toXML(afpChainOrig, ca1, ca2);
		
		
		//			assertEquals( xml, xmlComp);
		//StringManipulationTestsHelper.compareString(xml, xmlComp);
		
		StringManipulationTestsHelper.assertEqualsIgnoreEndline(xml, xmlComp);
		StructureAlignment ce = StructureAlignmentFactory.getAlgorithm(CeMain.algorithmName);

		System.out.println("re-running alignment...");
		AFPChain afpChainNew = ce.align(ca1,ca2);
		afpChainNew.setCalculationTime(-1);
		afpChainNew.setName1(name1);
		afpChainNew.setName2(name2);

		
		
		String xmlNew = AFPChainXMLConverter.toXML(afpChainNew,ca1,ca2);
		//String ce2 = afpChainNew.toFatcat(ca1, ca2);
		// FIXME version number, new line character difference?
		//			assertEquals(xml,xmlNew);
		StringManipulationTestsHelper.assertEqualsIgnoreEndline(xml,xmlNew);
		
		ChemCompGroupFactory.setChemCompProvider(ccprovorig);
		//assertEquals(ce1,ce2);

	}
}
