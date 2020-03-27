package info.dbaluxa;

import com.sun.codemodel.JFieldVar;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BindInfo;
import com.sun.xml.xsom.impl.AttributeUseImpl;
import com.sun.xml.xsom.impl.ParticleImpl;

import org.xml.sax.ErrorHandler;


/**
 * XJC plugin to place XSD documentation annotations ({@code <xs:annotation><xs:documentation>}) into Javadoc.
 *
 * @author Pavel Alexeev, Desi Balazs
 * @since 2019-01-17 03:34.
 */
public class XJCPluginDescriptionJavadoc extends Plugin {
	@Override
	public String getOptionName() {
		return "XPluginDescriptionJavadoc";
	}

	@Override
	public int parseArgument(Options opt, String[] args, int i) {
		return 0;
	}

	@Override
	public String getUsage() {
		return "  -XPluginDescriptionJavadoc    :  xjc plugin for bring XSD descriptions as Javadoc";
	}

	@Override
	public boolean run(Outline model, Options opt, ErrorHandler errorHandler) {
		model.getClasses().forEach(
			(ClassOutline c)-> {
				CClassInfo classInfo = c.target;
				String documentation = classInfoGetDescriptionAnnotation(classInfo);
				if(documentation!=null){
					c.implClass.javadoc().append(documentation);
				}

				c.implClass.fields().forEach((String name, JFieldVar jField) -> {
					classInfo.getProperties().stream()
							.filter(it-> it.getName(false).equals(jField.name()))
							.findAny()
							.map(property -> fieldGetDescriptionAnnotation(property))
							.ifPresent(fieldDocumentation -> jField.javadoc().append(fieldDocumentation));
				});
			}
		);

		return true;
	}


	static private String classInfoGetDescriptionAnnotation(CClassInfo classInfo){
		String description = "";
		if (null != (classInfo.getSchemaComponent()).getAnnotation()){
			description = ((BindInfo)(classInfo.getSchemaComponent()).getAnnotation().getAnnotation()).getDocumentation();
		}
		return description.trim();
	}

	static private String fieldGetDescriptionAnnotation(CPropertyInfo propertyInfo){
		String description = "";
		assert ( (propertyInfo.getSchemaComponent() instanceof AttributeUseImpl) || (propertyInfo.getSchemaComponent() instanceof ParticleImpl) );
		//<xs:complexType name="TDocumentRefer">
		//		<xs:attribute name="documentID" use="required">
		//			<xs:annotation>
		//				<xs:documentation>Идентификатор документа</xs:documentation>
		if ( (propertyInfo.getSchemaComponent() instanceof AttributeUseImpl)
				&& null != ( ((AttributeUseImpl)propertyInfo.getSchemaComponent()).getDecl().getAnnotation() )){
			description = ((BindInfo)((AttributeUseImpl)propertyInfo.getSchemaComponent()).getDecl().getAnnotation().getAnnotation()).getDocumentation();
		}
		// <xs:complexType name="TBasicInterdepStatement">
		//		<xs:element name="header" type="stCom:TInterdepStatementHeader" minOccurs="0">
		//				<xs:annotation>
		//					<xs:documentation>Заголовок заявления</xs:documentation>
		if ( (propertyInfo.getSchemaComponent() instanceof ParticleImpl)
				&& null != ( (((ParticleImpl) propertyInfo.getSchemaComponent()).getTerm()).getAnnotation() )){
			description = ((BindInfo)(((ParticleImpl) propertyInfo.getSchemaComponent()).getTerm()).getAnnotation().getAnnotation()).getDocumentation();
		}
		return description.trim();
	}


}
