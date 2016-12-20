class RubyServer
  include_package 'fatjar'

  def run()
    Server.create
        .filter("/*", lambda { |req, res| puts "Wildcard filter called" })
        .get("/", lambda { |req, res| res.setContent("Hello from ruby"); res.write })
        .listen(8080, "0.0.0.0").start
  end
end
RubyServer.new.run