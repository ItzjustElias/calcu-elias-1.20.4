package elias.calcu;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class CalcuClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerCalculatorCommand();
    }

    private void registerCalculatorCommand() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            final LiteralCommandNode<FabricClientCommandSource> calculateNode = ClientCommandManager
                    .literal("calculate")
                    .then(ClientCommandManager.argument("expression", StringArgumentType.greedyString())
                            .executes(context -> {
                                calculateExpression(context.getSource(), StringArgumentType.getString(context, "expression"));
                                return 1;
                            }))
                    .build();

            dispatcher.getRoot().addChild(calculateNode);

            // Register aliases for /calculate
            dispatcher.register(ClientCommandManager.literal("calc").redirect(calculateNode));
            dispatcher.register(ClientCommandManager.literal("c").redirect(calculateNode));

            // Register /calculate help command
            dispatcher.register(ClientCommandManager.literal("calculate")
                    .then(ClientCommandManager.literal("help")
                            .executes(context -> {
                                showHelpMenu(context.getSource());
                                return 1;
                            })
                    ));

            // Register /calc help command
            dispatcher.register(ClientCommandManager.literal("calc")
                    .then(ClientCommandManager.literal("help")
                            .executes(context -> {
                                showHelpMenu(context.getSource());
                                return 1;
                            })
                    ));

            // Register event handlers for functions and operators
            dispatcher.register(ClientCommandManager.literal("calc_functions")
                    .executes(context -> {
                        showFunctionsMenu(context.getSource());
                        return 1;
                    }));

            dispatcher.register(ClientCommandManager.literal("calc_operators")
                    .executes(context -> {
                        showOperatorsMenu(context.getSource());
                        return 1;
                    }));
        });
    }

    private void calculateExpression(FabricClientCommandSource source, String expression) {
        // replace custom expressions with their full forms
        expression = replaceCustomExpressions(expression);

        // evaluate expression
        try {
            Expression parsedExpression = new ExpressionBuilder(expression).build();
            final double result = parsedExpression.evaluate();

            // format result
            DecimalFormat decimalFormat = new DecimalFormat("0");
            decimalFormat.setMaximumFractionDigits(6);
            String formattedResult = decimalFormat.format(result);

            // send result to player
            source.sendFeedback(Text.literal("Result: " + formattedResult).formatted(Formatting.GREEN));
        } catch (Exception e) {
            source.sendFeedback(Text.literal("Error: " + e.getMessage()).formatted(Formatting.RED));
        }
    }

    private String replaceCustomExpressions(String expression) {
        // Map of custom expressions and their full forms
        Map<String, String> customExpressions = new HashMap<>();
        customExpressions.put("st", "64");

        // Replace custom expressions with their full forms
        for (Map.Entry<String, String> entry : customExpressions.entrySet()) {
            expression = expression.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue());
        }

        return expression;
    }

    private void showHelpMenu(FabricClientCommandSource source) {
        // Send help menu with clickable words
        Text helpMenu = Text.literal("Calcu Help Menu v0.0.1:").formatted(Formatting.GOLD)
                .append(Text.literal("\n---------------------------------------").formatted(Formatting.GRAY))
                .append(Text.literal("\n"))
                .append(Text.literal("FUNCTIONS").formatted(Formatting.BLUE)
                        .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/calc_functions"))))
                .append(Text.literal(" | "))
                .append(Text.literal("OPERATORS").formatted(Formatting.BLUE)
                        .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/calc_operators"))))
                .append(Text.literal("\n---------------------------------------").formatted(Formatting.GRAY));
        source.sendFeedback(helpMenu);
    }

    private void showFunctionsMenu(FabricClientCommandSource source) {
        // Send functions menu
        Text functionsMenu = Text.literal("Available Functions:").formatted(Formatting.GOLD)
                .append(Text.literal("\n---------------------------------------").formatted(Formatting.GRAY))
                .append(Text.literal("\n"))
                .append(Text.literal(Formatting.GREEN + "st : ").append("A Minecraft stack").formatted(Formatting.YELLOW))
                .append(Text.literal("\n"))
                .append(Text.literal(Formatting.GREEN + "sqrt : ").append("square root").formatted(Formatting.YELLOW))
                .append(Text.literal("\n"))
                .append(Text.literal(Formatting.GREEN + "abs : ").append("absolute value").formatted(Formatting.YELLOW))
                .append(Text.literal("\n"))
                .append(Text.literal(Formatting.GREEN + "sin : ").append("sin").formatted(Formatting.YELLOW))
                .append(Text.literal("\n"))
                .append(Text.literal(Formatting.GREEN + "cos : ").append("cosine").formatted(Formatting.YELLOW))
                .append(Text.literal("\n"))
                .append(Text.literal(Formatting.GREEN + "tan : ").append("tangent").formatted(Formatting.YELLOW))
                .append(Text.literal("\n"))
                .append(Text.literal(Formatting.GREEN + "log/log10/log2 : ").append("logarithm").formatted(Formatting.YELLOW))
                .append(Text.literal("\n"))
                .append(Text.literal(Formatting.GREEN + "ceil : ").append("nearest upper integer").formatted(Formatting.YELLOW))
                .append(Text.literal("\n"))
                .append(Text.literal(Formatting.GREEN + "floor : ").append("nearest lower integer").formatted(Formatting.YELLOW))
                .append(Text.literal("\n---------------------------------------").formatted(Formatting.GRAY));
        source.sendFeedback(functionsMenu);
    }

    private void showOperatorsMenu(FabricClientCommandSource source) {
        // Send operators menu
        Text operatorsMenu = Text.literal("Available Operators:").formatted(Formatting.GOLD)
                .append(Text.literal("\n---------------------------------------").formatted(Formatting.GRAY))
                .append(Text.literal("\n"))
                .append(Text.literal(Formatting.GREEN + "+ : ").append("Addition").formatted(Formatting.YELLOW))
                .append(Text.literal("\n"))
                .append(Text.literal(Formatting.GREEN + "- : ").append("Subtraction").formatted(Formatting.YELLOW))
                .append(Text.literal("\n"))
                .append(Text.literal(Formatting.GREEN + "* : ").append("Multiplication").formatted(Formatting.YELLOW))
                .append(Text.literal("\n"))
                .append(Text.literal(Formatting.GREEN + "/ : ").append("Division").formatted(Formatting.YELLOW))
                .append(Text.literal("\n"))
                .append(Text.literal(Formatting.GREEN + "^ : ").append("Exponentiation").formatted(Formatting.YELLOW))
                .append(Text.literal("\n"))
                .append(Text.literal(Formatting.GREEN + "% : ").append("Modulus").formatted(Formatting.YELLOW))
                .append(Text.literal("\n"))
                .append(Text.literal(Formatting.GREEN + "() : ").append("Parentheses").formatted(Formatting.YELLOW))
                .append(Text.literal("\n---------------------------------------").formatted(Formatting.GRAY));
        source.sendFeedback(operatorsMenu);
    }
}
