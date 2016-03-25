# encoding: utf-8

$:.unshift "#{ENV["LOGSTASH_HOME"]}/lib"

require "rubygems"
require "bootstrap/rubygems"
require "bootstrap/environment"
require "bootstrap/bundler"
require "logstash/namespace"
require "logstash/errors"
require "logstash/event"
require "logstash/config/file"
require "logstash/filters/base"
require "logstash/inputs/base"
require "logstash/outputs/base"

class LogStashPipelineRubyProxy
  def initialize(logstash_config)
    @logstash_config = logstash_config
    @logger = @logger = Cabin::Channel.get(LogStash)

    puts "========================================"
    puts "LOAD_PATH: #{$LOAD_PATH}"
    puts "GEM_HOME: #{ENV["GEM_HOME"]}"
    puts "========================================"

    eval_logstash_config
  end

  def plugin(plugin_type, name, *args)
    args << {} if args.empty?
    klass = LogStash::Plugin.lookup(plugin_type, name)
    klass.new(*args)
  end

  # Parser logstash config
  def eval_logstash_config
    # LogStash::Bundler.setup!({:without => [:build, :development]})
    grammar = LogStashConfigParser.new
    @logger.debug? && @logger.debug("Parsing logstash config: #{@logstash_config}")
    @configure = grammar.parse(@logstash_config)
    if @configure.nil?
      raise LogStash::ConfigurationError, grammar.failure_reason
    end
    # This will compile the config to ruby and evaluate the resulting code.
    # The code will initialize all the plugins and define the
    # filter and output methods.
    @code = @configure.compile
    # The config code is hard to represent as a log message...
    # So just print it.
    @logger.debug? && @logger.debug("Compiled pipeline code:\n#{@code}")
    begin
      eval(@code)
    rescue => e
      raise e
    end
  end

  def get_configurre
    @logstash_config
  end

  def get_parsed_configure
    @configure
  end

  def get_compiled_code
    @code
  end

  def get_filter_plugins
    @filters
  end

  def get_input_plugins
    @inputs
  end

  def get_output_plugins
    @outputs
  end
end