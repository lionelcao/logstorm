# encoding: utf-8
require 'spec_helper'
require "logstash/filters/siddhi"

describe LogStash::Filters::Siddhi do
  describe "Test siddhi filter query" do
    let(:config) do <<-CONFIG
      filter {
        siddhi {
          plan => "define stream StockExchangeStream (symbol string, price int, volume float );from StockExchangeStream[price >= 20 and price < 100] select symbol,volume insert into StockQuote;"
          expect  => ["StockQuote"]
        }
      }
    CONFIG
    end

    sample("symbol" => "ebay","price" => 80,"volumn" => 12.1,"type" => "StockExchangeStream") do
      expect(subject).to include("symbol")
      expect(subject["symbol"]).to eq("ebay")
      expect(subject["type"]).to eq("StockQuote")
    end
  end
end