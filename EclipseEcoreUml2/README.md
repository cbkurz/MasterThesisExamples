# EclipseEcoreUml2 

This module is for the purpose to try out how the UML2 package from Eclipse works.

It is adapted from the [UML2 example provided by the eclipse community](https://wiki.eclipse.org/MDT/UML2/Getting_Started_with_UML2)

The Module does load libraries from the local filesystem, since it was initially not possible to fetch the libraries from commonly known repositories.

In the eclipse infrastructure there is [Nexus](https://repo.eclipse.org/), [which might be the central repository for eclipse](https://wiki.eclipse.org/Services/Nexus) related projects.
Besides maven central, Nexus also knows some of the libraries.
However, in both cases the versions now in the filesystem could not be found.
In addition, at first glance, it seems that behind Nexus are actually several maven repositories which makes the process of fetching transitive repositories error-prone.

## Packages or Bundles

The Bundle version in the eclipse plugin description is 5.5.0 and two bundles are described:
* org.eclipse.uml2.uml
* org.eclipse.uml2.uml.resources

## Further Notes

More information can also be found in the [EMF FAQ](https://wiki.eclipse.org/EMF/FAQ).

The [git repository for UML2](https://git.eclipse.org/c/uml2/org.eclipse.uml2.git/) is not very active.

The eclipse page on [UML2](https://wiki.eclipse.org/MDT/UML2) also might contain some information.

Artifacts on Papyrus MARTE can be found [here](https://ci.eclipse.org/papyrus/job/papyrus-marte-2022-03/lastSuccessfulBuild/artifact/releng/org.eclipse.papyrus.marte.p2/target/repository/). \
The git of MARTE is [here](https://git.eclipse.org/c/papyrus/org.eclipse.papyrus-marte.git/)

The [EMF Repository](https://github.com/eclipse-emf/org.eclipse.emf/tree/master) is hosted on github on the account [eclipse-emf](https://github.com/eclipse-emf). 
The EMF Eclipse Ide can be downloaded [here](https://download.eclipse.org/modeling/emf/emf/builds/release/2.35.0/index.html)

In the Eclipse forum there might also be some interesting topics:
* [A Discussion about a UML2 standalone application](https://www.eclipse.org/forums/index.php/bm/msg/151687/1/on/0/?SQ=35770805adfefb0aae54b375a6846dcb)
* [Someone tried to create a UML Standalone](https://www.eclipse.org/forums/index.php/bm/msg/360956/1/on/0/?SQ=2342ad7b78fa5c74b0eb2b053daf9d3b)

