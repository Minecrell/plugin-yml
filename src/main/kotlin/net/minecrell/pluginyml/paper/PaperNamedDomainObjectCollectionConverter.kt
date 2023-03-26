package net.minecrell.pluginyml.paper

import com.fasterxml.jackson.databind.util.StdConverter
import org.gradle.api.NamedDomainObjectCollection

object PaperNamedDomainObjectCollectionConverter : StdConverter<NamedDomainObjectCollection<Any>, List<Any>>()  {
    override fun convert(value: NamedDomainObjectCollection<Any>): List<Any> {
        return value.toList()
    }
}