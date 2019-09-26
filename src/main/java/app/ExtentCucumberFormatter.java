package app;

import java.util.List;

import org.testng.ITestContext;

import com.intuit.karate.CallContext;
import com.intuit.karate.cucumber.FeatureWrapper;
import com.intuit.karate.cucumber.KarateReporter;

import functions.AppTest;
import gherkin.formatter.model.*;


//Author : John-Michael Leemnans
public class ExtentCucumberFormatter implements KarateReporter{

	ITestContext General_Data;
	Report report;
	

	public ExtentCucumberFormatter(Object object) {

		General_Data = ((AppTest) object).getContext();

		report = ((AppTest) object).getReport();

	}
	
	@Override
	public void syntaxError(String state, String event, List<String> legalEvents, String uri, Integer line) {
		
		System.out.println(state);
		
	}

	@Override
	public void uri(String uri) {
		
		System.out.println(uri);
		report.updateTestLog("URI", uri, Status.INFO);
	}

	@Override
	public void feature(Feature feature) {
	
		System.out.println(feature.toString());
	}

	@Override
	public void scenarioOutline(ScenarioOutline scenarioOutline) {
		
		System.out.println(scenarioOutline.getDescription());
	}

	@Override
	public void examples(Examples examples) {
	
		System.out.println(examples.toString());
	}

	@Override
	public void startOfScenarioLifeCycle(Scenario scenario) {
		
		System.out.println(scenario.toString());
	}

	@Override
	public void background(Background background) {
		
		System.out.println(background.toString());
	}

	@Override
	public void scenario(Scenario scenario) {
	
		System.out.println(scenario.toString());
	}

	@Override
	public void step(Step step) {
		
		System.out.println(step.toString());

	}

	@Override
	public void endOfScenarioLifeCycle(Scenario scenario) {
		
		System.out.println("eos");
	}

	@Override
	public void done() {
		
		System.out.println("done");
	}

	@Override
	public void close() {
		
		
	}

	@Override
	public void eof() {
		
		
	}

	@Override
	public void before(Match match, Result result) {
		
		System.out.println(match.toString());
	}

	@Override
	public void result(Result result) {
		
		System.out.println(result.toString());
		
		report.updateTestLog("Result", result.toString(), Status.INFO);
		
	}

	@Override
	public void after(Match match, Result result) {
		
		System.out.println(result.toString());
	}

	@Override
	public void match(Match match) {
	
		System.out.println(match.toString());
		report.updateTestLog("match","match location "+  match.getLocation(), Status.INFO);
	}

	@Override
	public void embedding(String mimeType, byte[] data) {
		
		
	}

	@Override
	public void write(String text) {
	
		System.out.println(text);
	}

	@Override
	public void callBegin(FeatureWrapper feature, CallContext callContext) {
	
		System.out.println("Karate  callBegin----" + feature.getText());
	}

	@Override
	public void karateStep(Step step, Match match, Result result, CallContext call) {

		Status stepStatus = null; 
		switch(result.getStatus().toString()) {
		case  "passed":
			stepStatus = Status.PASS;
			break;
		default:
		case "failed":
			stepStatus = Status.FAIL;
			report.updateTestLogNoScreenshot("KarateStep",  step.getName(),stepStatus );
			break;
		}

				
	}

	@Override
	public void karateStepProceed(Step step, Match match, Result result, CallContext call) {

	}
	
}
