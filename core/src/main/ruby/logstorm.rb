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

class CollectorQueueRubyProxy
  # java_import java.util.concurrent.SynchronousQueue
  # java_import java.util.concurrent.TimeUnit
  java_import com.ebay.logstorm.core.event.Collector

  def initialize(collector)
    @collector=collector
  end

  # Push an object to the queue if the queue is full
  # it will block until the object can be added to the queue.
  #
  # @param [Object] Object to add to the queue
  def push(obj)
    @collector.collect(obj)
  end

  alias_method(:<<, :push)

  # Offer an object to the queue, wait for the specified amout of time.
  # If adding to the queue was successfull it wil return true, false otherwise.
  #
  # @param [Object] Object to add to the queue
  # @param [Integer] Time in milliseconds to wait before giving up
  # @return [Boolean] True if adding was successfull if not it return false
  def offer(obj, timeout_ms)
    # @queue.offer(obj, timeout_ms, TimeUnit::MILLISECONDS)
    push(obj)
  end

  # # Blocking
  # def take
  #   @queue.take()
  # end
  #
  # # Block for X millis
  # def poll(millis)
  #   @queue.poll(millis, TimeUnit::MILLISECONDS)
  # end
end