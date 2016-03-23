require_relative "./logstorm"

pipeline_conf = %Q(
  input {
    generator {
      lines => [
        "line 1",
        "line 2",
        "line 3"
      ]
      count => 3
    }
  }

  output {
    stdout { codec => rubydebug }
  }
)

pipeline = LogStashPipelineProxy.new(pipeline_conf)
puts pipeline
puts pipeline.get_compiled_code
