ENV["LOGSTASH_HOME"] = "/Users/hchen9/Workspace/logstorm/build/vendor/logstash-2.2.0"
ENV["GEM_HOME"]= "#{ENV["LOGSTASH_HOME"]}/vendor/bundle/jruby/1.9"

require_relative "../logstorm"

puts "==============================="
puts "LogStash"
puts "==============================="
LogStash.constants.select {|c| puts(c) }

puts "==============================="
puts "LogStash::Inputs"
puts "==============================="
LogStash::Inputs.constants.select {|c| puts(c) }

puts "==============================="
puts "LogStash::Inputs"
puts "==============================="
LogStash::Inputs.constants.select {|c| puts(c) }