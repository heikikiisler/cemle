import chess

from cemle import config


def get_coefficients():
    with open(file=config.linear_regression_coefficients_path, mode="r") as file:
        file.readline()
        coefficients = file.readline().split(",")
        return [float(i) for i in coefficients]


LINEAR_REGRESSION_COEFFICIENTS = get_coefficients()
COEFFICIENTS_LENGTH = len(LINEAR_REGRESSION_COEFFICIENTS)


class BoardFeatureExtractor:
    PIECE_VALUES = {"P": 1, "p": -1, "B": 3, "b": -3, "N": 3, "n": -3,
                    "R": 5, "r": -5, "Q": 9, "q": -9, "K": 200, "k": -200}
    PIECES = ("Q", "q", "R", "r", "B", "b", "N", "n", "P", "p")

    def __init__(self, board=None, fen=None):
        self.fen = fen
        self.board = board if fen is None else board(fen=fen)
        self.pieces = self.board.piece_map().values()
        self.material_value = self.get_material_value()
        self.turn = self.board.turn

        self.moves = list(self.board.legal_moves)
        self.moves_total = len(self.moves)
        self.attacks_total = self.get_attacks_total(self.moves)

        self.board.push_uci("0000")  # Null move

        self.opponent_moves = list(self.board.legal_moves)
        self.opponent_moves_total = len(self.opponent_moves)
        self.opponent_attacks_total = self.get_attacks_total(self.opponent_moves)

        self.board.pop()  # Undo Null move

        self.white_moves_total = self.moves_total if self.turn else self.opponent_moves_total
        self.white_attacks_total = self.attacks_total if self.turn else self.opponent_attacks_total

        self.black_moves_total = self.moves_total if not self.turn else self.opponent_moves_total
        self.black_attacks_total = self.attacks_total if not self.turn else self.opponent_attacks_total

    def get_material_value(self):
        material_sum = 0
        for piece in self.pieces:
            material_sum += self.PIECE_VALUES[piece.symbol()]
        return material_sum

    def get_attacks_total(self, moves):
        return sum([self.board.is_capture(move) for move in moves])

    def get_castling_rights_sum(self):
        return (self.board.has_kingside_castling_rights(self.turn),
                self.board.has_queenside_castling_rights(self.turn)).count(True)

    def get_features(self):
        """Get features from board and return a dict."""
        features = {}
        if self.fen is not None:
            features.update({"fen": self.board.fen()})
        features.update({
            "material_total": self.material_value,
            "moves_total": self.white_moves_total - self.black_moves_total,
            "attacks_total": self.white_attacks_total - self.black_attacks_total,
        })
        return features

    @staticmethod
    def get_feature_keys():
        default_extractor = BoardFeatureExtractor(chess.Board())
        return list(default_extractor.get_features().keys())


def get_linear_regression_evaluation(board):
    extractor = BoardFeatureExtractor(board=board)
    features = list(extractor.get_features().values())[1:]
    evaluation = 0
    for i in range(0, COEFFICIENTS_LENGTH):
        evaluation += LINEAR_REGRESSION_COEFFICIENTS[i] * float(features[1])
    return evaluation
