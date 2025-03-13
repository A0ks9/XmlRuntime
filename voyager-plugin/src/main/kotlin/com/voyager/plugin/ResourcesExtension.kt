package com.voyager.plugin

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.ProjectLayout
import javax.inject.Inject

abstract class ResourcesExtension @Inject constructor(target: Project) {
    val resFiles: ConfigurableFileCollection = target.objects.fileCollection()
}
