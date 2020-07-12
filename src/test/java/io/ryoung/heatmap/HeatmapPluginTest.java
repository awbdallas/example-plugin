package io.ryoung.heatmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class HeatmapPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(HeatmapPlugin.class);

		List<String> additionalDevArguments = new ArrayList<>(Arrays.asList(args));
		additionalDevArguments.add("--developer-mode");
		additionalDevArguments.add("--debug");

		RuneLite.main(additionalDevArguments.toArray(new String[0]));
	}
}