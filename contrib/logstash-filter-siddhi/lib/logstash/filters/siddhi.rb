# encoding: utf-8
require "logstash/filters/base"
require "logstash/namespace"
require "java"
require "logstash-filter-siddhi_jars.rb"



class LogStash::Filters::Siddhi < LogStash::Filters::Base

  java_import "com.ebay.logstorm.contrib.logstash.LogStashSiddhiFilterImpl"

  # Setting the config_name here is required. This is how you
  # configure this filter from your Logstash config.
  #
  # filter {
  #   siddhi {
  #     plan => "Siddhi Execution Plan SQL",
  #     expect => ["Expected output stream names"]
  #   }
  # }
  #
  config_name "siddhi"

  # Replace the message with this value.
  config :query, :validate => :string, :default => nil
  config :export, :validate => :array, :default => nil

  public
  def register
    # Add instance variables
    @java_siddhi_filter = LogStashSiddhiFilterImpl.new(@query,@export)
    @java_siddhi_filter.register()
  end # def register

  public
  def filter(event)
    @java_siddhi_filter.filter(event)

    # filter_matched should go in the last line of our successful code
    filter_matched(event)
  end # def filter

  public
  def close()
    @java_siddhi_filter.close
  end

end # class LogStash::Filters::Example