//Contains the Definition of internal Feature object.
class Feature(){
    var name: String = ""
    var description: String = "" 
    var dockerfile_snippet: String = ""
    var test: String = ""
    var dependencies: List[Feature] = List()

    override def toString = s"Name: $name, Description: $description"

}


object FeatureTest{
    def main(args: Array[String]): Unit = {
        val feat = new Feature();
        feat.name = "Feature";
        feat.description = "This is My awesome Feature";
        println(feat.toString());
    }
}
