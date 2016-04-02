package com.ebay.logstorm.core;

import com.ebay.logstorm.core.compiler.PipelineCompiler;
import com.ebay.logstorm.core.exception.LogStormException;
import com.ebay.logstorm.core.runner.PipelineRunner;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipelineContextBuilder{
    private final PipelineContext pipelineContext;
    private PipelineRunner runner;
    private final static Logger LOG = LoggerFactory.getLogger(PipelineContextBuilder.class);
    private PipelineBuilder pipelineBuilder;

    public PipelineContextBuilder(){
        this.pipelineContext = new PipelineContext(null);
    }
    public PipelineContextBuilder(String pipeline){
        this.pipelineContext = new PipelineContext(pipeline);
    }
    public PipelineContextBuilder runner(PipelineRunner runner){
        this.runner = runner;
        return this;
    }

    public PipelineContextBuilder runner(Class<? extends PipelineRunner> runnerClass){
        try {
            this.runner = runnerClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public PipelineContextBuilder runner(String runnerClass){
        try {
            this.runner = (PipelineRunner) Class.forName(runnerClass).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * Keep single final submit method
     *
     * @throws LogStormException
     */
    public final void submit() throws LogStormException {
        if(this.runner == null){
            LOG.info("No runner set, use default runner: "+PipelineConstants.DEFAULT_RUNNER_CLASS_NAME);
            this.runner(PipelineConstants.DEFAULT_RUNNER_CLASS_NAME);
        }
        validate();
        this.runner.run(PipelineCompiler.compile(this.pipelineContext));
    }

    private void validate(){
        if(this.pipelineContext.getPipeline() != null && this.pipelineBuilder != null){
            throw new RuntimeException("Duplicated pipeline and pipelineBuilder");
        }
        if(this.pipelineBuilder!=null){
            this.pipelineContext.setPipeline(this.pipelineBuilder.build());
        }
    }

    public void submit(Class<? extends PipelineRunner> runnerClass) throws LogStormException {
        this.runner(runnerClass);
        submit();
    }

    public void submit(PipelineRunner runner) throws LogStormException {
        this.runner = runner;
        submit();
    }

    public PipelineContextBuilder setConfig(Config config) {
        this.pipelineContext.setConfig(config);
        return this;
    }

    public PipelineContextBuilder name(String pipelineName){
        this.pipelineContext.setPipelineName(pipelineName);
        return this;
    }

    public PipelineContextBuilder deploy(PipelineConstants.DeployMode mode){
        this.pipelineContext.setDeployMode(mode);
        return this;
    }

    public PipelineContextBuilder deploy(String mode){
        this.pipelineContext.setDeployMode(mode);
        return this;
    }

    public PipelineContextBuilder input(String... inputs){
        if(pipelineBuilder == null){
            this.pipelineBuilder = new PipelineBuilder();
        }
        for(String input:inputs) {
            pipelineBuilder.inputs.append(input);
        }
        return this;
    }

    public PipelineContextBuilder filter(String filter){
        if(pipelineBuilder == null){
            this.pipelineBuilder = new PipelineBuilder();
        }
        if(pipelineBuilder.filter!=null)
            throw new RuntimeException("Duplicated filters defined, existing: "+pipelineBuilder.filter +" , given: "+filter);
        pipelineBuilder.filter = filter;
        return this;
    }

    public PipelineContextBuilder output(String... outputs){
        if(pipelineBuilder == null){
            this.pipelineBuilder = new PipelineBuilder();
        }
        for(String output:outputs) {
            pipelineBuilder.outputs.append(output);
        }
        return this;
    }

    private static class PipelineBuilder{
        StringBuilder inputs = new StringBuilder();
        String filter;
        StringBuilder outputs  = new StringBuilder();

        public String build(){
            return String.format("input{%s} filter{%s} output{%s}",inputs.toString(),filter,outputs.toString());
        }
    }
}