package ir.ariwuh.plugin.ravensmp;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import lombok.val;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"UnstableApiUsage", "unused"})
public final class RavenSMPPluginLoader implements PluginLoader {

    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        val libraryResolver = new MavenLibraryResolver();

        libraryResolver.addRepository(
                new RemoteRepository
                        .Builder("central", "default", MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR)
                        .build()
        );

        libraryResolver.addDependency(
                new Dependency(
                        new DefaultArtifact("org.mongodb:mongodb-driver-sync:5.9.0"), null
                )
        );

        classpathBuilder.addLibrary(libraryResolver);
    }

}