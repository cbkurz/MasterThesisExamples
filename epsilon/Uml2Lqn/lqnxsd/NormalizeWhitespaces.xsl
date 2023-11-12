<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:this="urn:this-stylesheet"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xyz="urn:com.website/xyz" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                exclude-result-prefixes="xs this">
    <xsl:output method="xml" indent="yes"/>
    <!--Identity template, provides default behavior that copies all content into the output -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <!-- remove all text from the document -->
    <xsl:template match="text()">
        <xsl:value-of select="normalize-space(node())"/>
    </xsl:template>

</xsl:stylesheet>
