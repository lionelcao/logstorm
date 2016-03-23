class Panda
  def initialize(name="Po")
    @name = "Po"
  end
  def get_name
    @name
  end
  def set_name(name)
    @name = name
  end
  def eat
    puts "#{@name} eats dumplings ..."
  end
  def fight
    puts "#{@name} can kungfu ..."
  end
  def run
    puts "#{@name} is rolling ..."
  end
end