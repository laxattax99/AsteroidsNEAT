import processing.core.PApplet;

public class NN {

  int inputNodes, hiddenNodes, outputNodes;
  float learningRate = .3f;
  private PApplet sketch;


  Matrix InputToHiddenWs,
      HiddenToOutputWs,
      HiddenBias,
      OutputBias,
      input,
      hidden,
      output;

  public NN(PApplet sketch,NN copy) {
    this.sketch = sketch;
    inputNodes = copy.inputNodes;
    hiddenNodes = copy.hiddenNodes;
    outputNodes = copy.outputNodes;

    InputToHiddenWs = copy.InputToHiddenWs.copy();
    HiddenToOutputWs = copy.HiddenToOutputWs.copy();
    HiddenBias = copy.HiddenBias.copy();
    OutputBias = copy.OutputBias.copy();
  }

  public NN(PApplet sketch, int inputNodes, int hiddenNodes, int outputNodes) {
    this.sketch = sketch;
    this.inputNodes = inputNodes;
    this.hiddenNodes = hiddenNodes;
    this.outputNodes = outputNodes;

    InputToHiddenWs = Matrix.random(hiddenNodes, inputNodes);
    HiddenToOutputWs = Matrix.random(outputNodes, hiddenNodes);
    HiddenBias = Matrix.random(hiddenNodes, 1);
    OutputBias = Matrix.random(outputNodes, 1);
  }

  public NN(PApplet sketch, int inputNodes, int hiddenNodes, int outputNodes, float learningRate) {
    this(sketch, inputNodes, hiddenNodes, outputNodes);
    this.learningRate = learningRate;
  }

//  public float mut(float val, float rate){
//    if(sketch.random(1) < rate)
//  }

  public NN copy() {
    return new NN(this.sketch,this);
  }

  float sigmoid(float x) {
    return 1 / (1 + (float) Math.exp(-x));
  }

  private float[] activate(Matrix from, Matrix fromToWs, Matrix bias) {
    Matrix to = Matrix.Product(fromToWs, from);
    to.add(bias);

    for (int i = 0; i < to.rows; i++) {
      for (int j = 0; j < to.cols; j++) {
        float val = to.values[i][j];
        to.values[i][j] = sigmoid(val);
      }
    }

    return to.toArray();
  }

  float[] feedForward(float[] inputAr) {
    input = Matrix.FromArray(inputAr);
    hidden = Matrix.Product(InputToHiddenWs, input);
    hidden.add(HiddenBias);

    for (int i = 0; i < hidden.rows; i++) {
      for (int j = 0; j < hidden.cols; j++) {
        float val = hidden.values[i][j];
        hidden.values[i][j] = sigmoid(val);
      }
    }
//    hidden = Matrix.FromArray(activate(input, InputToHiddenWs, HiddenBias));

//    hidden output
    output = Matrix.Product(HiddenToOutputWs, hidden);
    output.add(OutputBias);

    for (int i = 0; i < output.rows; i++) {
      for (int j = 0; j < output.cols; j++) {
        float val = output.values[i][j];
        output.values[i][j] = sigmoid(val);
      }
    }
//    output = Matrix.FromArray(activate(hidden,HiddenToOutputWs,OutputBias));

    return output.toArray();
  }


  float dsigmoid(float y) {
    return y * (1 - y);
  }

  void train(float[] inputAr, float[] targetAr) {
    feedForward(inputAr);
    Matrix targets = Matrix.FromArray(targetAr);
    Matrix outputErr = Matrix.subtract(targets, output);

    Matrix gradient = output.copy();
    for (int i = 0; i < gradient.rows; i++) {
      for (int j = 0; j < gradient.cols; j++) {
        float val = gradient.values[i][j];
        gradient.values[i][j] = dsigmoid(val);
      }
    }

    gradient.multiply(outputErr);
    gradient.multiply(learningRate);

    Matrix hiddenT = Matrix.transpose(hidden);
    Matrix deltaHOWeights = Matrix.Product(gradient, hiddenT);

    HiddenToOutputWs.add(deltaHOWeights);

    OutputBias.add(gradient);

    Matrix HOWeightsT = Matrix.transpose(HiddenToOutputWs);
    Matrix hiddenErr = Matrix.Product(HOWeightsT, outputErr);

    Matrix hiddenGradient = hidden.copy();
    for (int i = 0; i < hiddenGradient.rows; i++) {
      for (int j = 0; j < hiddenGradient.cols; j++) {
        float val = hiddenGradient.values[i][j];
        hiddenGradient.values[i][j] = dsigmoid(val);
      }
    }

    hiddenGradient.multiply(hiddenErr);
    hiddenGradient.multiply(learningRate);

    Matrix inputT = Matrix.transpose(input);
    Matrix deltaIHWeights = Matrix.Product(hiddenGradient, inputT);

    InputToHiddenWs.add(deltaIHWeights);
    HiddenBias.add(hiddenGradient);
  }
  
  void mutate(float rate){
    for(int i = 0; i < InputToHiddenWs.rows; i++){
      for(int j = 0; j < InputToHiddenWs.cols;j++){
        float val = InputToHiddenWs.values[i][j];
        InputToHiddenWs.values[i][j] = mut(val,rate);
      }
    }

    for(int i = 0; i < HiddenToOutputWs.rows; i++){
      for(int j = 0; j < HiddenToOutputWs.cols;j++){
        float val = HiddenToOutputWs.values[i][j];
        HiddenToOutputWs.values[i][j] = mut(val,rate);
      }
    }

    for(int i = 0; i < HiddenBias.rows; i++){
      for(int j = 0; j < HiddenBias.cols;j++){
        float val = HiddenBias.values[i][j];
        HiddenBias.values[i][j] = mut(val,rate);
      }
    }

    for(int i = 0; i < OutputBias.rows; i++){
      for(int j = 0; j < OutputBias.cols;j++){
        float val = OutputBias.values[i][j];
        OutputBias.values[i][j] = mut(val,rate);
      }
    }
  }

  private float mut(float val, float rate){
    if (sketch.random(1) < rate){
      return (float) (val + sketch.randomGaussian() * .1);
    } else{
      return val;
    }
  }
}


